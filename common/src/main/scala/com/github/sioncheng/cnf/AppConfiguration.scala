package com.github.sioncheng.cnf

import spray.json._
import spray.json.DefaultJsonProtocol._

import scala.annotation.tailrec
import scala.io.Source


class AppConfiguration private (val jsValue: JsValue) {

    def getString(path: String): Option[String] = {
        get(path, jsValue).flatMap(x => {
            if (x.isInstanceOf[JsString]) {
                Some(x.asInstanceOf[JsString].value)
            } else {
                None
            }
        } )
    }

    def getInt(path: String): Option[Int] = {
        get(path, jsValue).flatMap(x => {
            if (x.isInstanceOf[JsNumber]) {
                Some(x.asInstanceOf[JsNumber].value.intValue())
            } else {
                None
            }
        })
    }

    @tailrec
    private def get(p: String, j: JsValue): Option[JsValue] = {
        val as = p.split("/")
        if (as.isEmpty) {
            None
        } else {
            val v = j.asInstanceOf[JsObject].getFields(as.head)
            if (v.isEmpty) {
                None
            } else {
                if(as.size == 1) {
                    Some(v.head)
                } else {
                    get(as.tail.mkString("/"), v.head)
                }
            }
        }
    }
}

object AppConfiguration {
    def apply(js: String): AppConfiguration =
        new AppConfiguration(js.parseJson)
}

object AppConfigurationLoader {
    def loadFromFile(path: String): AppConfiguration = {
        AppConfiguration(Source.fromFile(path).mkString)
    }

    def loadFromResourceFile(path: String): AppConfiguration = {
        loadFromFile(getClass.getResource(path).getPath)
    }
}