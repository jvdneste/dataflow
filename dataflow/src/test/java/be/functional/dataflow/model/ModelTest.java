package be.functional.dataflow.model;

import java.awt.BorderLayout;
import java.lang.reflect.InvocationTargetException;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import org.junit.Assert;
import org.junit.Test;

import com.google.common.base.Function;

import be.functional.dataflow.core.IDependent;
import be.functional.dataflow.core.IProperty;
import be.functional.dataflow.core.IValue;
import be.functional.dataflow.core.StmDomain;
import be.functional.dataflow.core.SwingDomain;
import be.functional.dataflow.core.SwingDomain.SideEffect;

public class ModelTest {

	@Test
	public void basicTest1() {

		final StmDomain domain = new StmDomain("test");

		final Model model = new Model(domain);

		final IProperty<Integer> b = model.<Integer>getProperty("a", "b");
		b.set(2);

		final IProperty<Integer> c = model.<Integer>getProperty("a", "c");
		c.set(3);

		final IValue<Integer> sum = domain.newExpression(new Function<IDependent, Integer>() {
			@Override
			public Integer apply(final IDependent dep) {
				return b.get(dep) + c.get(dep);
			}
		});

		Assert.assertEquals(sum.output(), (Integer) 5);

		final Model replacement = new Model(domain);
		replacement.getProperty("b").set(7);
		replacement.getProperty("c").set(9);
		model.getProperty("a").set(replacement);

		Assert.assertEquals((Integer) 7, b.output());
		Assert.assertEquals((Integer) 9, c.output());
		Assert.assertEquals((Integer) 16, sum.output());
	}

	@Test
	public void basicTest2() {

		final StmDomain domain = new StmDomain("test");

		final Model model = new Model(domain);

		final IProperty<Integer> c = model.<Integer>getProperty("a", "b", "c");
		c.set(2);

		final IProperty<Integer> d = model.<Integer>getProperty("a", "b", "d");
		d.set(3);

		final IValue<Integer> e = domain.newExpression(new Function<IDependent, Integer>() {
			@Override
			public Integer apply(final IDependent dep) {
				return c.get(dep) + d.get(dep);
			}
		});

		Assert.assertEquals(e.output(), (Integer) 5);

		final Model bReplacement = new Model(domain);
		bReplacement.getProperty("c").set(7);
		bReplacement.getProperty("d").set(9);
		model.getProperty("a", "b").set(bReplacement);

		Assert.assertEquals((Integer) 7, c.output());
		Assert.assertEquals((Integer) 9, d.output());
		Assert.assertEquals((Integer) 16, e.output());
	}

	@Test
	public void testUi() throws InvocationTargetException, InterruptedException {
		SwingUtilities.invokeAndWait(new Runnable() {

			@Override
			public void run() {
				final JFrame frame = new JFrame();
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.setLayout(new BorderLayout());

				final JPanel panel = new JPanel();
				panel.setLayout(new BorderLayout());

				final StmDomain stmDomain = new StmDomain("stmDomain");
				final IProperty<String> textProperty = stmDomain.newProperty("This is a new property");

				final SwingDomain swingDomain = new SwingDomain("swingDomain");

				final JTextField textField1 = new JTextField();
				final SideEffect sideEffect1 = swingDomain.bindTextField(textField1, textProperty);

				final JTextField textField2 = new JTextField();
				final SideEffect sideEffect2 = swingDomain.bindTextField(textField1, textProperty);

				panel.add(textField1, BorderLayout.NORTH);
				panel.add(textField2, BorderLayout.SOUTH);

				frame.getContentPane().add(panel, BorderLayout.CENTER);

				frame.pack();
				frame.setVisible(true);

				try {
					Thread.sleep(1000000);
				} catch (final InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}
}
