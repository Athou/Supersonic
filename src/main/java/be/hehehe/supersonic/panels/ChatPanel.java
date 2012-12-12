package be.hehehe.supersonic.panels;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingWorker;

import net.miginfocom.swing.MigLayout;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jdesktop.swingx.JXErrorPane;
import org.jdesktop.swingx.JXTable;
import org.subsonic.restapi.ChatMessage;
import org.subsonic.restapi.Response;

import be.hehehe.supersonic.model.ChatMessageModel;
import be.hehehe.supersonic.model.ChatMessageTableModel;
import be.hehehe.supersonic.service.SubsonicService;
import be.hehehe.supersonic.service.SubsonicService.Param;
import be.hehehe.supersonic.utils.SupersonicException;

@SuppressWarnings("serial")
@Singleton
public class ChatPanel extends JPanel {

	@Inject
	SubsonicService subsonicService;

	@Inject
	Logger log;

	private JTextField messageText;
	private JButton sendButton;
	private ChatMessageTableModel tableModel;
	private JXTable table;

	@PostConstruct
	public void init() {
		buildFrame();
		attachBehavior();
		startRefreshThread();
	}

	private void buildFrame() {
		setLayout(new MigLayout("fill"));
		add(new JLabel("Message: "));
		add(messageText = new JTextField(), "growx, push");
		add(sendButton = new JButton("Send"), "wrap");

		table = new JXTable();
		tableModel = new ChatMessageTableModel();
		table.setModel(tableModel);
		table.setCellSelectionEnabled(false);
		table.setRowSelectionAllowed(false);
		table.setColumnSelectionAllowed(false);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.setColumnControlVisible(true);
		table.setFillsViewportHeight(true);
		table.setFocusable(false);
		add(new JScrollPane(table), "span, grow");

	}

	private void attachBehavior() {
		sendButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				sendMessage();
			}
		});
		messageText.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					sendMessage();
				}
			}
		});

	}

	private void sendMessage() {
		new SwingWorker<Object, Void>() {

			@Override
			protected Object doInBackground() throws Exception {
				String message = messageText.getText();
				sendButton.setEnabled(false);
				if (StringUtils.isNotBlank(message)) {
					try {
						subsonicService.invoke("addChatMessage", new Param(
								"message", message));
					} catch (SupersonicException e) {
						JXErrorPane.showDialog(e);
					}
				}
				return null;
			}

			@Override
			protected void done() {
				sendButton.setEnabled(true);
				messageText.setText(null);
				refreshMessages();
			};
		}.execute();
	}

	private void startRefreshThread() {
		Runnable thread = new Runnable() {
			@Override
			public void run() {
				refreshMessages();
			}
		};
		Executors.newScheduledThreadPool(1).scheduleAtFixedRate(thread, 5, 10,
				TimeUnit.SECONDS);

	}

	private void refreshMessages() {
		try {
			Response response = subsonicService.invoke("getChatMessages");
			List<ChatMessage> messages = response.getChatMessages()
					.getChatMessage();
			tableModel.clear();
			for (ChatMessage message : messages) {
				tableModel.add(new ChatMessageModel(message.getUsername(),
						message.getTime(), message.getMessage()));
			}
			table.packAll();
		} catch (SupersonicException e) {
			log.error("Could not refresh messages", e);
		}
	}
}
