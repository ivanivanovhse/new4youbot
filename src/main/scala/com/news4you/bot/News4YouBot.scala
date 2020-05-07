package com.news4you.bot
import canoe.api.{Scenario, TelegramClient, _}
import canoe.models.Chat
import canoe.syntax._
import com.news4you.{Document, PageRequest}
import com.news4you.news.News
import zio.Task
import zio._
import zio.interop.catz._

object News4YouBot {
    def news4YouBotScenario[F[_]: TelegramClient]: Scenario[F, Unit] =
        for {
            chat <- Scenario.expect(command("news").chat)
            _ <- Scenario.eval(chat.send( "Where do you want to get the news from"))
            link <- Scenario.expect(text)
            _ <- provideNews(chat,link)
        } yield ()

    def provideNews[F[_]: TelegramClient](chat:Chat, link:String): Scenario[F, Unit] ={
        for{
            docs <- Scenario.eval(News.documents(PageRequest(link)))
            _ <- Scenario.eval(chat.send("Your news:"))
            _ <- Scenario.eval(chat.send(docs.toString))
        } yield ()


    }
}
