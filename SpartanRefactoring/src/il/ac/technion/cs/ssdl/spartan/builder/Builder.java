package il.ac.technion.cs.ssdl.spartan.builder;

import il.ac.technion.cs.ssdl.spartan.refactoring.All;
import il.ac.technion.cs.ssdl.spartan.refactoring.Spartanization;
import il.ac.technion.cs.ssdl.spartan.utils.Range;
import il.ac.technion.cs.ssdl.spartan.utils.Utils;

import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.CompilationUnit;

/**
 * @author Eclipse (auto-generated)
 * @author Boris van Sosin <code><boris.van.sosin [at] gmail.com></code>
 * @since 2013/07/01
 */
public class Builder extends IncrementalProjectBuilder {
	/**
	 * the ID under which this builder is registered
	 */
	public static final String BUILDER_ID = "il.ac.technion.cs.ssdl.spartan.builder.Builder";
	private static final String MARKER_TYPE = "il.ac.technion.cs.ssdl.spartan.spartanizationSuggestion";
	/**
	 * the key in the marker's properties map under which the type of the
	 * spartanization is stored
	 */
	public static final String SPARTANIZATION_TYPE_KEY = "il.ac.technion.cs.ssdl.spartan.spartanizationType";

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.internal.events.InternalBuilder#build(int,
	 * java.util.Map, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override protected IProject[] build(final int kind, @SuppressWarnings({ "unused", "rawtypes" }) final Map args,
	    final IProgressMonitor m) throws CoreException {
		if (m != null)
			m.beginTask("Checking for spartanization opportunities", IProgressMonitor.UNKNOWN);
		build(kind);
		if (m != null)
			m.done();
		return null;
	}

	private void build(final int kind) throws CoreException {
		System.err.println("Spartan building");
		if (kind == FULL_BUILD)
			fullBuild();
		else {
			final IResourceDelta d = getDelta(getProject());
			if (d == null)
				fullBuild();
			else
				incrementalBuild(d);
		}
	}

	protected void fullBuild() throws CoreException {
		getProject().accept(new IResourceVisitor() {
			@Override public boolean visit(final IResource r) throws CoreException {
				addMarkers(r);
				return true; // to continue visiting children.
			}
		});
	}

	static void addMarkers(final IResource r) throws CoreException {
		if (r instanceof IFile && r.getName().endsWith(".java"))
			addMarkers((IFile) r);
	}

	private static void addMarkers(final IFile f) throws CoreException {
		deleteMarkers(f);
		addMarkers(f, (CompilationUnit) Utils.makeParser(JavaCore.createCompilationUnitFrom(f)).createAST(null));
	}

	private static void addMarkers(final IFile f, final CompilationUnit cu) throws CoreException {
		for (final Spartanization s : All.all())
			for (final Range r : s.findOpportunities(cu))
				if (r != null)
					addMarker(f, s, r);
	}

	private static void addMarker(final IFile f, final Spartanization s, final Range r) throws CoreException {
		addMarker(f.createMarker(MARKER_TYPE), s, r);

	}

	private static void addMarker(final IMarker m, final Spartanization s, final Range r) throws CoreException {
		m.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_INFO);
		addMarker(m, s);
		addMarker(m, r);
	}

	private static void addMarker(final IMarker m, final Range r) throws CoreException {
		m.setAttribute(IMarker.CHAR_START, r.from);
		m.setAttribute(IMarker.CHAR_END, r.to);
	}

	private static void addMarker(final IMarker m, final Spartanization s) throws CoreException {
		m.setAttribute(SPARTANIZATION_TYPE_KEY, s.toString());
		m.setAttribute(IMarker.MESSAGE, "Spartanization suggestion: " + s.getMessage());
	}

	/**
	 * deletes all spartanization suggestion markers
	 * 
	 * @param f
	 *          the file from which to delete the markers
	 * @throws CoreException
	 *           if this method fails. Reasons include: This resource does not
	 *           exist. This resource is a project that is not open. Resource
	 *           changes are disallowed during certain types of resource change
	 *           event notification. See {@link IResourceChangeEvent} for more
	 *           details.
	 */
	public static void deleteMarkers(final IFile f) throws CoreException {
		f.deleteMarkers(MARKER_TYPE, false, IResource.DEPTH_ONE);
	}

	protected static void incrementalBuild(final IResourceDelta d) throws CoreException {
		d.accept(new IResourceDeltaVisitor() {
			@Override public boolean visit(final IResourceDelta internalDelta) throws CoreException {
				switch (internalDelta.getKind()) {
				case IResourceDelta.ADDED:
				case IResourceDelta.CHANGED:
					// handle added and changed resource
					addMarkers(internalDelta.getResource());
					break;
				case IResourceDelta.REMOVED:
					// handle removed resource
					break;
				default:
					break;
				}
				// return true to continue visiting children.
				return true;
			}
		});
	}
}