package com.news4you.telegram

import canoe.api.{TelegramClient => Client}
import com.news4you.telegram.CanoeScenarios.CanoeScenarios
import zio._
import zio.logging.{Logger, Logging}

object TelegramClient {
    type TelegramClient = Has[Service]

    trait Service {
        def start: Task[Unit]
    }

    type CanoeDeps = Has[Client[Task]] with Logging with CanoeScenarios

    def canoe: URLayer[CanoeDeps, Has[Service]] =
        ZLayer.fromServices[Client[Task], Logger[String], CanoeScenarios.Service, Service] { (client, logger, scenarios) =>
            Canoe(logger, scenarios, client)
        }

    def empty: ULayer[Has[Service]] =
        ZLayer.succeed(new Service {
            override def start: Task[Unit] = ???

            // override def broadcastMessage(receivers: Set[ChatId], message: String): Task[Unit] = ???
        })
}
