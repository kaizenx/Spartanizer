package il.ac.technion.cs.ssdl.spartan.commandhandlers;

import il.ac.technion.cs.ssdl.spartan.refactoring.InlineSingleUse;

/**
 * a handler for {@link InlineSingleUse}
 * 
 * @author Boris van Sosin <code><boris.van.sosin [at] gmail.com></code>
 * @author Yossi Gil <code><yossi.gil [at] gmail.com></code> (major refactoring
 *         2013/07/11)
 * @since 2013/07/01
 */
public class InlineSingleUseHandler extends BaseHandler {
	/** Instantiates this class */
	public InlineSingleUseHandler() {
		super(new InlineSingleUse());
	}
}