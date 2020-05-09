package com.news4you.telegram

import canoe.api.{TelegramClient => Client, _}
import zio.Task
import zio.interop.catz._
import zio.logging.Logger

private[telegram] final case class Canoe(
                                            logger: Logger[String],
                                            scenarios: CanoeScenarios.Service,
                                            canoeClient: Client[Task]
                                        ) extends TelegramClient.Service {

    implicit val canoe: Client[Task] = canoeClient

    /*  def broadcastMessage(receivers: Set[ChatId], message: String): Task[Unit] =
        ZIO
          .foreach(receivers) { chatId =>
            val api = new ChatApi(PrivateChat(chatId.value, None, None, None))
            api.send(TextContent(message))
          }
          .unit*/

    override def start: Task[Unit] =
        logger.info("Starting Telegram polling") *>
            Bot.polling[Task]
                .follow(
                    scenarios.news /*,
          scenarios.help,
          scenarios.add,
          scenarios.del,
          scenarios.list*/
                )
                .compile
                .drain
                .catchAllCause{cause=>
                    logger.error("Error during Bot Scenario",cause)
                }

                .ignore
               /* .catchAll(_ => {
                    logger.info("Error during Bot Scenario"/*, error*/)
                   // UIO.succeed(_)
                }).ignore*/
}
