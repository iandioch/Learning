package striking.learning.math

import striking.learning.Implicits

import scala.collection.LinearSeq
import Implicits._

case class Fib(n: BigInt, m: BigInt, _newFibsFib: (Fib, Int) => BigInt = null) extends Cloneable {
	private val FibHelper = {
		class FibHelper {
			def newFibsFibDefault(fib: Fib, x: Int): BigInt = {
				if (x == 0) {
					fib.n
				} else if (x == 1) {
					fib.m
				} else if (x < 0) {
					fib.get(x + 2) - fib.get(x + 1)
				} else {
					fib.get(x - 1) + fib.get(x - 2)
				}
			}
		}

		new FibHelper()
	}

	val newFibsFib = if (_newFibsFib == null) FibHelper.newFibsFibDefault _ else _newFibsFib

	private val newFibs: Int => BigInt = newFibsFib(this, _)

	def get: (Int => BigInt) = list.andNega(negaList)

	def list: LinearSeq[BigInt] = MathRef.whole.map(newFibs)
	def negaList: LinearSeq[BigInt] = MathRef.negative.map(newFibs)

	def regenWithGen(regen: Fib => (BigInt, BigInt)): Fib = {
		val (a, b) = regen(this)
		new Fib(a, b)
	}

	def regenWithIndex(a: Int, b: Int, regen: (BigInt => BigInt)): Fib = {
		regenWithGen(gen => {
			(regen(gen.get(a)), regen(gen.get(b)))
		})
	}

	def regenWith: (BigInt => BigInt) => Fib = regenWithIndex(0, 1, _)

	def map(f: BigInt => BigInt): Fib = {
		def nextFibsFib(fib: Fib, x: Int): BigInt = {
			f(newFibsFib(fib, x))
		}
		Fib(n, m, nextFibsFib)
	}

	def pisano(y: Int): Fib = {
		def modY(x: BigInt): BigInt = {
			x % y
		}
		map(modY)
	}
}