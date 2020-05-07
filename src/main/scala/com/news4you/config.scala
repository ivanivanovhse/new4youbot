package com.news4you

import pureconfig.generic.semiauto._
import pureconfig.{ConfigConvert, ConfigSource}
import zio.{Has, ZIO, ZLayer}

object config {

    final case class Config(appConfig: AppConfig,
                            news4YouConfig: News4YouConfig,
                            botConfig: BotConfig)

    object Config {
        implicit val convert: ConfigConvert[Config] = deriveConvert
    }

    type ConfigProvider = Has[Config]

    object ConfigProvider {

        val live: ZLayer[Any, IllegalStateException, Has[Config]] =
            ZLayer.fromEffect {
                ZIO
                    .fromEither(ConfigSource.default.load[Config])
                    .mapError(
                        failures =>
                            new IllegalStateException(
                                s"Error loading configuration: $failures"
                            )
                    )
            }
    }

    type AppConfigProvider = Has[AppConfig]

    object AppConfigProvider {
        val fromConfig: ZLayer[ConfigProvider, Nothing, AppConfigProvider] =
            ZLayer.fromService(_.appConfig)
    }

    final case class AppConfig(port: Int,
                               baseUrl: String)

    object AppConfig {
        implicit val convert: ConfigConvert[AppConfig] = deriveConvert
    }

    final case class BotConfig(token: String)

    object BotConfig {
        implicit val convert: ConfigConvert[BotConfig] = deriveConvert
    }

    final case class DBConfig(
                                 url: String,
                                 driver: String,
                                 user: String,
                                 password: String)

    object DBConfig {
        implicit val convert: ConfigConvert[DBConfig] = deriveConvert
    }


    final case class News4YouConfig(url: String)

    object News4YouConfig {
        implicit val convert: ConfigConvert[News4YouConfig] = deriveConvert
    }

}
