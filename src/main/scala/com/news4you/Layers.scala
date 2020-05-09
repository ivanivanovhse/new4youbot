package com.news4you

import canoe.api.{TelegramClient => CanoeClient}
import com.news4you.config.{AppConfigProvider, ConfigProvider}
import com.news4you.http.HttpClient
import com.news4you.http.HttpClient.{ClientTask, HttpClient}
import com.news4you.telegram.TelegramClient.TelegramClient
import com.news4you.telegram.{CanoeScenarios, TelegramClient}
import org.http4s.client.blaze.BlazeClientBuilder
import zio._
import zio.blocking.Blocking
import zio.clock.Clock
import zio.interop.catz._
import zio.logging.Logging
import zio.logging.slf4j.Slf4jLogger

import scala.concurrent.ExecutionContext.Implicits

object Layers {
    type ConfigurationEnv = ConfigProvider with Logging with Clock with Blocking
    type AppConfigurationEnv = ConfigurationEnv with AppConfigProvider with ClientTask
    type News4YouEnv = AppConfigurationEnv with HttpClient with TelegramClient

    object live {
        private def makeHttpClient =
            ZIO.runtime[Any].map { implicit rts =>
                BlazeClientBuilder
                    .apply[Task](Implicits.global)
                    .resource
                    .toManaged
            }

        val configurationEnv: ZLayer[Blocking, Throwable, ConfigurationEnv] =
            Blocking.any ++ Clock.live ++ ConfigProvider.live ++ Slf4jLogger.make((_, msg) => msg)

        val httpEnv = ZLayer.fromManaged(makeHttpClient.toManaged_.flatten)

        val appConfigurationEnv: ZLayer[ConfigurationEnv, Throwable, AppConfigurationEnv] =
            AppConfigProvider.fromConfig ++ httpEnv ++ ZLayer.identity

        val httpClientEnv = (configurationEnv ++ appConfigurationEnv) >>> HttpClient.http4s


        val canoeEnv: ZLayer[ConfigProvider, Throwable, Has[CanoeClient[Task]]] = {
            ZLayer.fromManaged(ZIO.runtime[Any]
                .map { implicit rts =>
                    for {
                        token <- ZIO.access[ConfigProvider](_.get.botConfig.token)
                    } yield CanoeClient.global[Task](token).toManaged
                }.flatten.toManaged_.flatten)
        }

        val canoeScenarioEnv =
            (canoeEnv ++ httpClientEnv) >>> CanoeScenarios.live

        val telegramClientEnv = (configurationEnv ++ canoeScenarioEnv ++ canoeEnv) >>> TelegramClient.canoe


        val appLayer: ZLayer[Blocking, Throwable, News4YouEnv] =
            configurationEnv >>> appConfigurationEnv ++ httpClientEnv ++ telegramClientEnv
    }

}
