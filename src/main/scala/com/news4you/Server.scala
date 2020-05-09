package com.news4you

import com.news4you.config._
import com.news4you.telegram.TelegramClient.TelegramClient
import zio._
import zio.console.putStrLn

object Server extends App {
    type AppTask[A] = RIO[Layers.News4YouEnv , A]
   // type Task[A] = RIO[Layers.News4YouEnv, A]
   // type BotTask[A] = RIO[Clock with HttpClient, A]
    override def run(args: List[String]): ZIO[ZEnv, Nothing, Int] = {
        val prog =
            for {
                cfg <- ZIO.access[ConfigProvider](_.get)
                _ <- logging.log.info(s"Starting with $cfg")
                canoe<- ZIO.access[TelegramClient](_.get)
                _ <-canoe.start.ignore.as(0)/*.fork*/
              /*  botConfig = cfg.botConfig
                canoeClient <-makeCanoeClient(botConfig.token)
                _ <- runTelegramBot(botConfig.token)*/
            } yield 0

        prog.provideLayer(Layers.live.appLayer).catchAll(error => putStrLn(error.toString).as(1))/*.foldM(
           err => putStrLn(s"Execution failed with: ${err.getMessage}") *> ZIO.succeed(1),
           _ => ZIO.succeed(0)
       )*//*.orDie*//*.orDie*/
    }


   /* private def makeCanoeClient(token: String): UIO[TaskManaged[CanoeClient[Task]]] =
        ZIO
            .runtime[Any]
            .map { implicit rts =>
                CanoeClient
                    .global[Task](token)
                    .toManaged
            }*/

/*    def runTelegramBot[R<:Clock](token: String) = {
       // type Task[A] = UIO[ A]

       // type Task[A] = RIO[R, A]

       // type BotTask[A] = RIO[R, A]

        ZIO.runtime[R].flatMap { implicit rts =>
            Stream
                .resource(TelegramClient.global[Task](token))
                .flatMap { implicit client =>
                    Bot.polling[Task].follow(News4YouBot.news4YouBotScenario)
                }
                .compile.drain.as(ExitCode.Success)
        }
    }*/
}
