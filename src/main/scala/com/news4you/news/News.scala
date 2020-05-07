package com.news4you.news

import com.news4you.http.HttpClient.{HttpClient, get}
import com.news4you.http.implicits.HttpRequestOps
import com.news4you.{Document, PageRequest}
import io.circe.generic.auto._
import zio.RIO

object News {
    def documents(request: PageRequest): RIO[HttpClient, List[Document]]=
        get[Document]("/news4You/news",request.parameters)
}
