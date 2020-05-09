package com.news4you.http

import com.news4you.config.{Config, ConfigProvider}
import io.circe.Decoder
import org.http4s.client.Client
import zio._
import zio.logging.{Logger, Logging}

object HttpClient {
    type HttpClient = Has[Service]

    type ClientTask = Has[Client[Task]]

    type Response[T] = RIO[HttpClient, T]

    trait Service {
        def rootUrl: String
        def get[T](uri: String, parameters: Map[String, String])
                  (implicit d: Decoder[T]): Task[T]
    }

   /* def get[T](resource: String, parameters: Map[String, String] = Map())
              (implicit d: Decoder[T]):Task[T]=
     RIO.accessM[HttpClient](_.get.get[List[T]](resource, parameters))

    def get[T](resource: String, id: Long)
              (implicit d: Decoder[T]): Task[T] =
        get[T](resource, id.toString)

    def get[T](resource: String, pathParameter: String)
              (implicit d: Decoder[T]): Task[T] =
        RIO.accessM[HttpClient](_.get.get[T](s"$resource/$pathParameter", Map()))

    def get[T](resource: String, pathParameter: String, parameters: Map[String, String])
              (implicit d: Decoder[T]): Task[List[T]] =
        get[T](s"$resource/$pathParameter", parameters)*/

    def http4s: URLayer[ConfigProvider with Logging with ClientTask, HttpClient] =
        ZLayer.fromServices[Config, Logger[String], Client[Task], Service] { (config, logger, http4sClient) =>
            Http4s(config.news4YouConfig.url, logger, http4sClient)
        }
}