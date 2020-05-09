package com.news4you.telegram

import canoe.api._
import canoe.models.Chat
import canoe.syntax._
import com.news4you.http.HttpClient
import com.news4you.{Document, PageRequest}
import zio._
import io.circe.generic.auto._
import com.news4you.http.implicits.HttpRequestOps
/*private[scenario] */ final case class Live(httpClient: HttpClient.Service,
                                             canoeClient: TelegramClient[Task]
                                            ) extends CanoeScenarios.Service {

    private implicit val client: TelegramClient[Task] = canoeClient

    override def news: Scenario[Task, Unit] = {
        def provideNews(chat: Chat, link: String): Scenario[Task, Unit] = {
            for {
                result <- Scenario.eval(news(PageRequest(link))).attempt
                _ <- result.fold(
                    _ => Scenario.eval(chat.send("Something went wrong while making your order. Please try again.")),
                    docs =>
                       for{
                            _ <- Scenario.eval(chat.send("Your news:"))
                            _ <- Scenario.eval(chat.send(docs.toString))
                        } yield ()
                       // Scenario.eval(chat.send(s"Order successfully made. Here's your order id: $orderId"))
                )
                /*_ <- Scenario.eval(chat.send("Your news:"))
                _ <- Scenario.eval(chat.send(docs.toString))*/
            } yield ()
        }

        def news(pageRequest: PageRequest): Task[List[Document]] = {
            httpClient.get[List[Document]]("/news4You/news", pageRequest.parameters)
            //Task.succeed(List(Document(pageRequest.pageLink,"content")))
        }

        for {
            chat <- Scenario.expect(command("news").chat)
            _ <- Scenario.eval(chat.send("Where do you want to get the news from"))
            link <- Scenario.expect(text)
            _ <- provideNews(chat, link)
        } yield ()
    }
    def processError=
        Task.succeed("Something went wrong while making your order. Please try again.")
      //  Sync[F].pure("Something went wrong while making your order. Please try again.")
}
