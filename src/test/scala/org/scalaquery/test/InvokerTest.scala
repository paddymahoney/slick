package org.scalaquery.test

import scala.collection.mutable.ArrayBuffer
import org.junit.Test
import org.junit.Assert._
import org.scalaquery.ql._
import org.scalaquery.ql.extended.{ExtendedTable => Table}
import org.scalaquery.session.Database.threadLocalSession
import org.scalaquery.test.util._
import org.scalaquery.test.util.TestDB._

object InvokerTest extends DBTestObject(H2Mem)

class InvokerTest(tdb: TestDB) extends DBTest(tdb) {
  import tdb.driver.Implicit._

  @Test def testCollections() {

    val T = new Table[Int]("t") {
      def a = column[Int]("a")
      def * = a
    }

    db withSession {
      T.ddl.create
      T.insertAll(2, 3, 1, 5, 4)

      val q = for {
        t <- T
        _ <- Query orderBy t.a
      } yield t.a

      val r1 = q.list
      val r1t: List[Int] = r1
      assertEquals(List(1, 2, 3, 4, 5), r1)

      val r2 = q.to[List]()
      val r2t: List[Int] = r2
      assertEquals(List(1, 2, 3, 4, 5), r2)

      val r3 = q.to[Set]()
      val r3t: Set[Int] = r3
      assertEquals(Set(3, 4, 2, 1, 5), r3)

      val r4 = q.to[IndexedSeq]()
      val r4t: IndexedSeq[Int] = r4
      assertEquals(IndexedSeq(1, 2, 3, 4, 5), r4)

      val r5 = q.to[ArrayBuffer]()
      val r5t: ArrayBuffer[Int] = r5
      assertEquals(ArrayBuffer(1, 2, 3, 4, 5), r5)

      val r6 = q.to[Array]()
      val r6t: Array[Int] = r6
      assertEquals(Array(1, 2, 3, 4, 5).toList, r6.toList)
    }
  }

  @Test def testMap() {

    val T = new Table[(Int, String)]("t") {
      def k = column[Int]("k")
      def v = column[String]("v")
      def * = k ~ v
    }

    db withSession {
      T.ddl.create
      T.insertAll(2 -> "b", 3 -> "c", 1 -> "a")

      val q = Query(T)

      val r1 = q.toMap
      val r1t: Map[Int, String] = r1
      assertEquals(Map(1 -> "a", 2 -> "b", 3 -> "c"), r1)
    }
  }
}