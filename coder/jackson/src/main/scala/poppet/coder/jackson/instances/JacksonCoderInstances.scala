package poppet.coder.jackson.instances

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import com.fasterxml.jackson.module.scala.ScalaObjectMapper
import poppet.coder.Coder
import poppet.coder.jackson.instances.JacksonCoderInstances._
import scala.language.experimental.macros
import scala.reflect.macros.blackbox

trait LpJacksonCoderInstances {
    implicit val om: ObjectMapper = {
        val om = new ObjectMapper() with ScalaObjectMapper
        om.registerModule(DefaultScalaModule)
        om
    }

    implicit def anyToJsonCoder[A](implicit om: ObjectMapper): Coder[A, JsonNode] =
        a => om.valueToTree(a)
    implicit def jsonToAnyCoder[A](implicit om: ObjectMapper): Coder[JsonNode, A] = macro jsonToAnyCoderImpl[A]
}

trait JacksonCoderInstances extends LpJacksonCoderInstances {
    implicit def bytesToJsonCoder(implicit om: ObjectMapper): Coder[Array[Byte], JsonNode] =
        a => om.readTree(a)
    implicit def jsonToBytesCoder(implicit om: ObjectMapper): Coder[JsonNode, Array[Byte]] =
        a => om.writeValueAsBytes(a)
}

object JacksonCoderInstances {
    def jsonToAnyCoderImpl[A](
        c: blackbox.Context)(om: c.Expr[com.fasterxml.jackson.databind.ObjectMapper])(implicit AT: c.WeakTypeTag[A]
    ): c.universe.Tree = {
        import c.universe._
        val atype = AT.tpe
        q"""new _root_.poppet.coder.Coder[_root_.com.fasterxml.jackson.databind.JsonNode, $atype] {
            def apply(a: _root_.com.fasterxml.jackson.databind.JsonNode): $atype = $om.readValue(
                $om.treeAsTokens(a),
                new _root_.com.fasterxml.jackson.core.`type`.TypeReference[$atype] {}
            )
        }"""
    }
}