package io.pig.protosearch

import cats.data.NonEmptyList
import io.pig.lucille.Query

object BooleanQuery {

  def search(index: TermIndexArray, q: Query): Either[String, List[(Int, Double)]] =
    q match {
      case Query.OrQ(qs) => onlyTerms(qs).map(qs => BooleanQueryImpl.OrQ(qs).search(index))
      case Query.AndQ(qs) => onlyTerms(qs).map(qs => BooleanQueryImpl.AndQ(qs).search(index))
      case _ => Left("Bro, c'mon, only ORs and ANDs, thank you")
    }

  private def onlyTerms(qs: NonEmptyList[Query]): Either[String, NonEmptyList[String]] =
    qs.traverse {
      case t: Query.TermQ => Right(t.q)
      case _ => Left("Sorry bucko, only term queries supported today")
    }
}

object BooleanQueryImpl {
  case class OrQ(terms: NonEmptyList[String]) {

    def search(index: TermIndexArray): List[(Int, Double)] = {
      val docs: Set[Int] = terms.toList.map(t => index.docsWithTermSet(t)).reduce(_ union _)
      terms.toList
        .flatMap(t => index.scoreTFIDF(docs, t))
        .groupMapReduce(_._1)(_._2)(_ + _)
        .toList
    }
  }

  case class AndQ(terms: NonEmptyList[String]) {

    def search(index: TermIndexArray): List[(Int, Double)] = {
      val docs: Set[Int] = terms.toList.map(t => index.docsWithTermSet(t)).reduce(_ intersect _)
      terms.toList
        .flatMap(t => index.scoreTFIDF(docs, t))
        .groupMapReduce(_._1)(_._2)(_ + _)
        .toList
    }
  }
}
