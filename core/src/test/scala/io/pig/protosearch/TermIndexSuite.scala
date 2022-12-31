package io.pig.protosearch

class TermIndexSuite extends munit.FunSuite {
  def tokenize(s: String): List[String] =
    s.split(" ").toList

  val docs = List(
    tokenize("the quick brown fox jumped over the lazy cat"),
    tokenize("the very fast cat jumped across the room"),
    tokenize("a lazy cat sleeps all day"),
  )

  lazy val index = TermIndex(docs)

  test("apply builds from list of lists of strings") {
    assertEquals(index.numTerms, 16)
  }

  test("docCount returns zero when no docs contain term") {
    assertEquals(index.docCount("???"), 0)
  }

  test("docCount returns number of documents containing term") {
    assertEquals(index.docCount("cat"), 3)
    assertEquals(index.docCount("lazy"), 2)
    assertEquals(index.docCount("room"), 1)
  }

  test("docsWithTerm returns empty list when no docs contain term") {
    assertEquals(index.docsWithTerm("???"), Nil)
  }

  test("docsWithTerm returns list of docIDs containing term") {
    assertEquals(index.docsWithTerm("cat").sorted, List(0, 1, 2))
    assertEquals(index.docsWithTerm("the").sorted, List(0, 1))
    assertEquals(index.docsWithTerm("lazy").sorted, List(0, 2))
  }

  test("docsWithTermTFIDF returns list of docIDs and tf-idf scores") {
    val samples = List(
      tokenize("this is a sample"),
      tokenize("this is another example"),
    )
    val index = TermIndex(samples)
    assertEquals(index.docsWithTermTFIDF("example").sorted, List((1, 0.6931471805599453)))
  }

  test("docsWithTermTFIDF returns list of docIDs containing term in order of TFIDF") {
    assertEquals(index.docsWithTermTFIDF("lazy").map(_._1), List(2, 0))
  }

}