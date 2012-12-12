package be.hehehe.supersonic;

import javax.inject.Inject;
import javax.swing.JComponent;
import javax.swing.RepaintManager;
import javax.swing.SwingUtilities;

import org.jboss.weld.environment.se.Weld;

public class Main {

	@Inject
	Supersonic supersonic;

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				RepaintManager
						.setCurrentManager(new CheckThreadViolationRepaintManager());
				new Weld().initialize().instance().select(Main.class).get();
			}
		});
	}

	private static class CheckThreadViolationRepaintManager extends
			RepaintManager {
		private boolean completeCheck = true;

		public synchronized void addInvalidComponent(JComponent component) {
			checkThreadViolations(component);
			super.addInvalidComponent(component);
		}

		public void addDirtyRegion(JComponent component, int x, int y, int w,
				int h) {
			checkThreadViolations(component);
			super.addDirtyRegion(component, x, y, w, h);
		}

		private void checkThreadViolations(JComponent c) {
			if (!SwingUtilities.isEventDispatchThread()
					&& (completeCheck || c.isShowing())) {
				Exception exception = new Exception();
				boolean repaint = false;
				boolean fromSwing = false;
				StackTraceElement[] stackTrace = exception.getStackTrace();
				for (StackTraceElement st : stackTrace) {
					if (repaint && st.getClassName().startsWith("javax.swing.")) {
						fromSwing = true;
					}
					if ("repaint".equals(st.getMethodName())) {
						repaint = true;
					}
				}
				if (repaint && !fromSwing) {
					// no problems here, since repaint() is thread safe
					return;
				}
				exception.printStackTrace();
			}
		}
	}
}
