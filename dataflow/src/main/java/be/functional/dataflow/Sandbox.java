package be.functional.dataflow;

import scala.concurrent.stm.Ref.View;
import scala.concurrent.stm.japi.STM;

public class Sandbox {

	public static void main(final String[] args) {

		final View<Integer> value = STM.newRef(1);
	}
}
