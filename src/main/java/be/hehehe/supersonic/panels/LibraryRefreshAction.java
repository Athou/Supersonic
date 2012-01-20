package be.hehehe.supersonic.panels;

import java.awt.event.ActionEvent;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.swing.AbstractAction;

import org.jdesktop.swingx.JXErrorPane;

import be.hehehe.supersonic.service.Library;
import be.hehehe.supersonic.utils.SupersonicException;

@SuppressWarnings("serial")
@Singleton
public class LibraryRefreshAction extends AbstractAction {

	@Inject
	Library library;

	@Inject
	LibraryRefreshDialog dialog;

	@Override
	public void actionPerformed(ActionEvent e) {
		try {
			library.refresh();
			dialog.setVisible(true);
		} catch (SupersonicException e1) {
			dialog.dispose();
			JXErrorPane.showDialog(e1);
		}
	}
}
