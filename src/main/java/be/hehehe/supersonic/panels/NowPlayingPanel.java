package be.hehehe.supersonic.panels;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingWorker;

import net.miginfocom.swing.MigLayout;

import org.apache.log4j.Logger;
import org.subsonic.restapi.NowPlayingEntry;
import org.subsonic.restapi.Response;

import be.hehehe.supersonic.events.SongEvent;
import be.hehehe.supersonic.events.SongEvent.Type;
import be.hehehe.supersonic.model.NowPlayingModel;
import be.hehehe.supersonic.service.CoverArtService;
import be.hehehe.supersonic.service.SubsonicService;
import be.hehehe.supersonic.utils.SupersonicException;
import be.hehehe.supersonic.utils.SwingUtils;
import be.hehehe.supersonic.utils.WrapLayout;

import com.google.common.collect.Lists;

@SuppressWarnings("serial")
@Singleton
public class NowPlayingPanel extends JPanel {

	@Inject
	CoverArtService coverArtService;

	@Inject
	SubsonicService subsonicService;

	@Inject
	Logger log;

	@Inject
	Event<SongEvent> event;

	@PostConstruct
	public void init() {
		setLayout(new WrapLayout(FlowLayout.LEFT, 10, 10));
		startRefreshThread();
	}

	private void startRefreshThread() {
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				refresh();
			}
		};
		Executors.newScheduledThreadPool(1).scheduleAtFixedRate(runnable, 5,
				10, TimeUnit.SECONDS);

	}

	private void refresh() {
		new SwingWorker<Object, Void>() {

			private List<NowPlayingModel> list = Lists.newArrayList();

			@Override
			protected Object doInBackground() throws Exception {
				try {
					Response response = subsonicService.invoke("getNowPlaying");
					for (NowPlayingEntry entry : response.getNowPlaying()
							.getEntry()) {
						list.add(new NowPlayingModel(entry.getId(), entry
								.getUsername(), entry.getArtist(), entry
								.getTitle(), entry.getMinutesAgo(),
								coverArtService.getCoverImage(entry
										.getCoverArt())));
					}
				} catch (SupersonicException e) {
					log.error(e.getMessage(), e);
				}
				return null;
			}

			@Override
			protected void done() {
				removeAll();
				for (NowPlayingModel model : list) {
					add(new IndividualPlayingPanel(model));
				}
			}
		}.execute();

	}

	private class IndividualPlayingPanel extends JPanel {

		public IndividualPlayingPanel(final NowPlayingModel model) {
			setLayout(new MigLayout("insets 0", "[][grow]", "[][][][]"));
			setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

			JLabel userNameLabel = new JLabel(model.getUserName());
			Font font = userNameLabel.getFont();
			userNameLabel.setFont(new Font(font.getName(), Font.BOLD, font
					.getSize()));
			add(userNameLabel, "cell 0 0");

			JLabel artistLabel = new JLabel(model.getArtist());
			font = artistLabel.getFont();
			artistLabel.setFont(new Font(font.getName(), Font.ITALIC, font
					.getSize()));
			add(artistLabel, "cell 0 1");

			add(new JLabel(model.getTitle()), "cell 0 2, top");
			add(new JLabel(model.getMinutesAgo() <= 5 ? ""
					: model.getMinutesAgo() + " minutes ago"), "cell 0 3, top");
			add(new IndividualPlayingCoverPanel(model.getImage()),
					"cell 1 0 1 4, grow");

			addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					SongEvent songEvent = new SongEvent(Type.CHANGE_SELECTION);
					songEvent.setSong(model);
					event.fire(songEvent);
				}
			});
		}
	}

	private class IndividualPlayingCoverPanel extends JPanel {

		private BufferedImage image;

		public IndividualPlayingCoverPanel(BufferedImage image) {
			this.image = image;
			setPreferredSize(new Dimension(100, 100));
		}

		@Override
		protected void paintComponent(Graphics g) {
			SwingUtils.drawImage(image, g, this);
		}
	}
}
