package com.news4you.telegram

import canoe.api.{TelegramClient => Client, _}
import com.news4you.http.HttpClient
import com.news4you.http.HttpClient.HttpClient
import zio._

object CanoeScenarios {
  type CanoeScenarios = Has[Service]

  trait Service {
    //def start: Scenario[Task, Unit]
   // def help: Scenario[Task, Unit]

    def news: Scenario[Task, Unit]
   /* def del: Scenario[Task, Unit]
    def list: Scenario[Task, Unit]*/
  }

  type LiveDeps = Has[Client[Task]]  with HttpClient
  def live: URLayer[LiveDeps,Has[Service]] =
    ZLayer.fromServices[Client[Task], HttpClient.Service,Service] {
      (client,httpClient) =>
        Live(httpClient,client)
    }
}
