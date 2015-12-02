package be.functional.dataflow.core;

import java.lang.ref.WeakReference;

import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.JTextComponent;

import com.google.common.base.Objects;

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

	public abstract class SideEffect implements IDependent, Runnable {

		@Override
		public IDomain getDomain() {
			return SwingDomain.this;
		}
	}

	private static void perform(final Runnable effect) {
		if (SwingUtilities.isEventDispatchThread()) {
			effect.run();
		} else {
			SwingUtilities.invokeLater(effect);
		}
	}

	public SideEffect bindTextField(final JTextComponent textComponent, final IProperty<String> property) {
		class TextFieldUpdater extends SideEffect {
			@Override
			public void run() {
				final String value = property.get(this);
				if (!Objects.equal(value, textComponent.getText())) {
					textComponent.setText(value);
				}
			}
		}

		final TextFieldUpdater updater = new TextFieldUpdater();
		perform(updater);

		class Adapter implements DocumentListener {
			@Override
			public void removeUpdate(final DocumentEvent e) {
				property.set(textComponent.getText());
			}

			@Override
			public void insertUpdate(final DocumentEvent e) {
				property.set(textComponent.getText());
			}

			@Override
			public void changedUpdate(final DocumentEvent e) {
			}
		};
		final Adapter listener = new Adapter();
		textComponent.getDocument().addDocumentListener(listener);

		return updater;
	}

	//	public void bindComponent()(final IBean pBean, final String pPropertyName, final IProperty<String> pProperty) {
	//
	//		final IDependent dependent = new SideEffect() {
	//			@Override
	//			public void apply() {
	//
	//			};
	//		};
	//
	//		final String value = pProperty.get(dependent);
	//
	//		class TextFieldBinding implements SideEffect1<IDependent>, PropertyChangeListener {
	//
	//			private volatile boolean _updating = false;
	//
	//			@Override
	//			public void apply(final IDependent pDependant) {
	//				if (_updating) {
	//					return;
	//				}
	//				_updating = true;
	//				SwingUtilities.invokeLater(new Runnable() {
	//					@Override
	//					public void run() {
	//						final String value = pPropertyByPath.get(pDependant);
	//						logger.debug(MessageFormat.format("updating component property. '{0}' -> '{1}'", pPropertyName, value));
	//						Beans.setProperty(pBean, pPropertyName, value);
	//						_updating = false;
	//					}
	//				});
	//			}
	//
	//			@Override
	//			public void propertyChange(final PropertyChangeEvent evt) {
	//				if (_updating) {
	//					return;
	//				}
	//				_updating = true;
	//				final String value = (String) Beans.getProperty(pBean, pPropertyName);
	//				logger.debug(MessageFormat.format("updating dependable property. '{0}' -> '{1}'", pPropertyName, value));
	//				pPropertyByPath.set(value);
	//				_updating = false;
	//			}
	//		}
	//		final TextFieldBinding binding = new TextFieldBinding();
	//		pBean.addPropertyChangeListener(pPropertyName, binding);
	//		return SideEffects.Perform(binding);
	//	}

	// away with expression, just define 'bindings' of 'side effects', or something like that.

	@Override
	public void signal(final Iterable<WeakReference<IDependent>> dependers) {
		perform(new Runnable() {
			@Override
			public void run() {
				for (final WeakReference<IDependent> ref : dependers) {
					final IDependent dependent = ref.get();
					if (dependent != null) {
						((SideEffect)dependent).run();
					}
				}
			}
		});
	}
}
