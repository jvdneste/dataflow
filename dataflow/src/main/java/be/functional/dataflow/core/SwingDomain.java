package be.functional.dataflow.core;

import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;
import java.util.Queue;

import javax.annotation.Nullable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.google.common.base.Function;
import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;

/** only hosts side effects */
public class SwingDomain implements IDomain {

	private final String name;

	public SwingDomain(final String name) {
		this.name = name;
	}

	@Override
	public String getName() {
		return name;
	}

	private abstract class SideEffect implements IDependent {

		@Override
		public IDomain getDomain() {
			return SwingDomain.this;
		}

		public abstract void apply();
	}

	public void bindTextField(final JTextField textField, final IProperty<String> value) {
		class Adapter implements DocumentListener {
			@Override
			public void removeUpdate(final DocumentEvent e) {
			}

			@Override
			public void insertUpdate(final DocumentEvent e) {
			}

			@Override
			public void changedUpdate(final DocumentEvent e) {
			}
		};
		final Adapter listener = new Adapter();
		textField.getDocument().addDocumentListener(listener);
	}

	public void bindComponent()(final IBean pBean, final String pPropertyName, final IProperty<String> pProperty) {

		final IDependent dependent = new SideEffect() {
			@Override
			public void apply() {

			};
		};

		final String value = pProperty.get(dependent);

		class TextFieldBinding implements SideEffect1<IDependant>, PropertyChangeListener {

			private volatile boolean _updating = false;

			@Override
			public void apply(final IDependant pDependant) {
				if (_updating) {
					return;
				}
				_updating = true;
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						final String value = pPropertyByPath.get(pDependant);
						logger.debug(MessageFormat.format("updating component property. '{0}' -> '{1}'", pPropertyName, value));
						Beans.setProperty(pBean, pPropertyName, value);
						_updating = false;
					}
				});
			}

			@Override
			public void propertyChange(final PropertyChangeEvent evt) {
				if (_updating) {
					return;
				}
				_updating = true;
				final String value = (String) Beans.getProperty(pBean, pPropertyName);
				logger.debug(MessageFormat.format("updating dependable property. '{0}' -> '{1}'", pPropertyName, value));
				pPropertyByPath.set(value);
				_updating = false;
			}
		}
		final TextFieldBinding binding = new TextFieldBinding();
		pBean.addPropertyChangeListener(pPropertyName, binding);
		return SideEffects.Perform(binding);
	}

	// away with expression, just define 'bindings' of 'side effects', or something like that.

	@Override
	public void signal(final Iterable<WeakReference<IDependent>> dependers) {
		if (SwingUtilities.isEventDispatchThread()) {
		} else {
		}
	}
}
