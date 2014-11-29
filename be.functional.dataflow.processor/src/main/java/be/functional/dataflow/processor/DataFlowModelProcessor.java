package be.functional.dataflow.processor;

import java.util.Collection;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.ElementFilter;
import javax.tools.Diagnostic;

import be.functional.dataflow.DataFlowModel;

import com.google.auto.service.AutoService;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;

@AutoService(Processor.class)
public class DataFlowModelProcessor extends AbstractProcessor {

	@Override
	public Set<String> getSupportedAnnotationTypes() {
		return ImmutableSet.of(DataFlowModel.class.getName());
	}

	@Override
	public SourceVersion getSupportedSourceVersion() {
		return SourceVersion.latestSupported();
	}

	@Override
	public boolean process(final Set<? extends TypeElement> annotations, final RoundEnvironment roundEnv) {
		if ((annotations.size() == 1) && Iterables.getOnlyElement(annotations).getQualifiedName().toString().equals(DataFlowModel.class.getName())) {
			process(roundEnv);
		}
		return false;
	}

	private void process(final RoundEnvironment roundEnv) {
		final Collection<? extends Element> annotatedElements = roundEnv.getElementsAnnotatedWith(DataFlowModel.class);
		final Collection<? extends TypeElement> types = ElementFilter.typesIn(annotatedElements);
		for (final TypeElement type : types) {
			try {
				processType(type);
			} catch (final AbortProcessingException e) {
				// We abandoned this type, but continue with the next.
			} catch (final RuntimeException e) {
				// Don't propagate this exception, which will confusingly crash the compiler.
				// Instead, report a compiler error with the stack trace.
				final String trace = Throwables.getStackTraceAsString(e);
				reportError("@DataFlowModel processor threw an exception: " + trace, type);
			}
		}
	}

	private void processType(final TypeElement type) {
		reportWarning("bla bla" + type.getSimpleName().toString(), type);
	}

	private void reportWarning(final String msg, final Element e) {
		processingEnv.getMessager().printMessage(Diagnostic.Kind.WARNING, msg, e);
	}

	private void reportError(final String msg, final Element e) {
		processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, msg, e);
	}
}
