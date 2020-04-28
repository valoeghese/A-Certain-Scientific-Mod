package tk.valoeghese.tknm;

import java.util.Random;

import tk.valoeghese.tknm.api.OrderedList;

public class TestCases {
	public static void main(String[] args) {
		TestOrderedList.test();
	}

	public static class TestOrderedList {
		static void test() {
			OrderedList<Obj> order = new OrderedList<>(Obj::getVal);
			Random rand = new Random();

			final int number = 10;

			long ns = System.nanoTime();
			Obj[] objects = new Obj[number];
			for (int i = 0; i < number; ++i) {
				objects[i] = new Obj((rand.nextFloat() - 0.5f) * 30f);
			}

			ns = System.nanoTime() - ns;
			System.out.println("Generated " + objects.length + " dummy objects and stored in an object array in " + (double)ns * 0.000001 + "ms.");
			ns = System.nanoTime();

			for (Obj obj : objects) {
				order.add(obj);
			}

			ns = System.nanoTime() - ns;

			System.out.println("Transferred " + order.size() + " dummy objects from the object array to an ordered list in " + (double)ns * 0.000001 + "ms.");
		}

		static class Obj {
			Obj(float val) {
				this.val = val;
			}

			private final float val;

			public float getVal() {
				return this.val;
			}

			@Override
			public String toString() {
				return "TestObj{" + this.val + "}";
			}
		}
	}
}
