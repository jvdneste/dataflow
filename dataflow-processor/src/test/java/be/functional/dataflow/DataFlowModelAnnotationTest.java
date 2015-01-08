package be.functional.dataflow;

import org.junit.Test;


@DataFlowModel
interface TestModel {

	int whatever();
	int whatever2();
}

public class DataFlowModelAnnotationTest {

	@Test
	public void test1() {

	}
}
