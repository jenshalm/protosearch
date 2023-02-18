/*
 * Copyright 2022 CozyDev
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package pink.cozydev.protosearch

import TokenStream.tokenizeSpaceV

class TermIndexArraySuite extends munit.FunSuite {

  val index = CatIndex.index

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
    val samples = Vector(
      tokenizeSpaceV("this is a sample"),
      tokenizeSpaceV("this is another example"),
    )
    val index = TermIndexArray(samples)
    assertEquals(index.docsWithTermTFIDF("example").sorted, List((1, 0.6931471805599453)))
  }

  test("docsWithTermTFIDF returns list of docIDs containing term in order of TFIDF") {
    assertEquals(index.docsWithTermTFIDF("lazy").map(_._1), List(2, 0))
  }

}