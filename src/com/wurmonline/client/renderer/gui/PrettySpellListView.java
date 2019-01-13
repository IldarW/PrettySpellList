package com.wurmonline.client.renderer.gui;

import com.wurmonline.client.renderer.backend.Queue;
import com.wurmonline.client.resources.textures.ImageTexture;
import com.wurmonline.client.resources.textures.ImageTextureLoader;
import com.wurmonline.client.resources.textures.Texture;
import net.Ildar.wurm.PrettySpellListMod;
import net.Ildar.wurm.SpellAction;
import org.gotti.wurmunlimited.modloader.classhooks.HookManager;

import javax.imageio.ImageIO;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PrettySpellListView extends ContainerComponent {
    private static final int ICON_SIZE = 32;
    private static final int BOTTOM_PADDING = 16;
    private static final int SIDE_STICHES_WIDTH = 13;
    private static final Map<SpellAction, BufferedImage> cachedSpellIcons = new HashMap<>();
    private static Texture verticalTilingTexture;

    private List<SpellButton> spellButtons;
    private int rowSize;
    private int rowCount;

    public PrettySpellListView(List<SpellAction> spellActions, int x, int y, int rowSize, SpellButtonListener buttonListener) {
        super("Spell list");
        spellButtons = new ArrayList<>();
        this.rowSize = rowSize;
        this.rowCount = (int) Math.ceil((float) spellActions.size() / rowSize);
        for (SpellAction spellAction : spellActions) {
            BufferedImage spellIconImage = getSpellIconImage(spellAction);
            if (spellIconImage == null) {
                Logger.getLogger(PrettySpellListMod.class.getSimpleName()).log(Level.WARNING, "Can't get a spell image!");
                continue;
            }
            ImageTexture spellIconTexture = ImageTextureLoader.loadNowrapNearestTexture(spellIconImage, true);
            spellButtons.add(new SpellButton(spellAction.getDescription(), ICON_SIZE, ICON_SIZE, spellIconTexture, new ButtonListener() {
                @Override
                public void buttonPressed(WButton p0) {
                }

                @Override
                public void buttonClicked(WButton p0) {
                    buttonListener.onClick(spellAction);
                }
            }));
        }
        setInitialSize(rowSize * ICON_SIZE + SIDE_STICHES_WIDTH, rowCount * ICON_SIZE + 2 * SIDE_STICHES_WIDTH + BOTTOM_PADDING, false);
        setPosition(x - width, y);
        layout();
        this.sizeFlags = FlexComponent.FIXED_WIDTH | FlexComponent.FIXED_HEIGHT;
        prepareVerticalTilingTexture();
    }

    private void prepareVerticalTilingTexture() {
//        if (verticalTilingTexture != null)
//            return;
        try {
            BufferedImage image = ImageIO.read(WurmComponent.panelTextureTilingH.getUrl().openStream());
            AffineTransform transform = new AffineTransform();
            transform.rotate(-Math.PI / 2, image.getWidth() / 2, image.getHeight() / 2);
            AffineTransformOp op = new AffineTransformOp(transform, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
            image = op.filter(image, null);
            verticalTilingTexture = ImageTextureLoader.loadNowrapMipmapTexture(image, false);
        } catch (IOException e) {
            Logger.getLogger(PrettySpellListMod.class.getSimpleName()).log(Level.WARNING, e.getMessage(), e);
        }
    }

    private URL getResourceUrl(String resourceName) {
        URL url = this.getClass().getClassLoader().getResource(resourceName);
        if (url == null && this.getClass().getClassLoader() == HookManager.getInstance().getLoader()) {
            url = HookManager.getInstance().getClassPool().find(PrettySpellListMod.class.getName());
            if (url != null) {
                String path = url.toString();
                int pos = path.lastIndexOf('!');
                if (pos != -1) {
                    path = path.substring(0, pos) + "!/" + resourceName;
                }
                try {
                    return new URL(path);
                } catch (MalformedURLException e) {
                    Logger.getLogger(PrettySpellListMod.class.getSimpleName()).log(Level.WARNING, e.getMessage(), e);
                }
            }
        }
        return url;
    }


    private BufferedImage getSpellIconImage(SpellAction spellAction) {
        if (cachedSpellIcons.containsKey(spellAction))
            return cachedSpellIcons.get(spellAction);
        try {
            URL url = getResourceUrl(spellAction.name() + ".png");
            if (url != null) {
                try {
                    BufferedImage spellImage = ImageIO.read(url);
                    cachedSpellIcons.put(spellAction, spellImage);
                    return spellImage;
                } catch (Exception e) {
                    if (!spellAction.equals(SpellAction.UnknownSpell))
                        return getSpellIconImage(SpellAction.UnknownSpell);
                }
            }
        } catch (Exception e) {
            Logger.getLogger(PrettySpellListMod.class.getSimpleName()).log(Level.WARNING, e.getMessage(), e);
        }
        return null;
    }

    @Override
    void childResized(FlexComponent p0) {
    }

    @Override
    void performLayout() {
        int width = ICON_SIZE * rowSize;
        int height = ICON_SIZE * rowCount;
        for (int i = 0; i < spellButtons.size(); ++i) {
            int column = i % rowSize;
            int row = i / rowSize;
            final int xx = width * column / rowSize;
            final int yy = height * row / rowCount;
            final int ww = width * (column + 1) / rowSize - xx;
            final int hh = height * (row + 1) / rowCount - yy;
            SpellButton spellButton = spellButtons.get(i);
            if (spellButton != null)
                spellButton.setLocation(this.x + xx + SIDE_STICHES_WIDTH, this.y + yy + SIDE_STICHES_WIDTH, ww, hh);
        }
    }

    @Override
    public void gameTick() {
    }

    @Override
    protected void renderComponent(Queue queue, float alpha) {
        this.drawTexTilingH(queue, WurmComponent.panelTextureTilingH, this.r, this.g, this.b, 1.0f,
                this.x + 3, this.y,
                this.width - 3, SIDE_STICHES_WIDTH,
                128, SIDE_STICHES_WIDTH);
        this.drawTexTilingH(queue, WurmComponent.panelTextureTilingH, this.r, this.g, this.b, 1.0f,
                this.x + 3, this.y + this.height - BOTTOM_PADDING - SIDE_STICHES_WIDTH,
                this.width - 3, BOTTOM_PADDING,
                141, BOTTOM_PADDING);
        this.drawTexture(queue, verticalTilingTexture, this.r, this.g, this.b, 1.0f,
                this.x, this.y + 4,
                SIDE_STICHES_WIDTH, this.height - BOTTOM_PADDING - 9,
                128, 4,
                SIDE_STICHES_WIDTH, this.height - BOTTOM_PADDING - 9);
        final int wBg = this.width;
        final int hBg = this.height - 2 * SIDE_STICHES_WIDTH - BOTTOM_PADDING;
        final float u = wBg / 64.0f;
        final float v = hBg / 64.0f;
        this.drawTexture(queue, WurmPopup.backgroundTexture2, this.r, this.g, this.b, 1.0f,
                this.x + SIDE_STICHES_WIDTH, this.y + SIDE_STICHES_WIDTH,
                wBg, hBg, 0, 0,
                (int) (u * 256.0f), (int) (v * 256.0f));
        for (SpellButton spellButton : spellButtons) {
            if (spellButton != null)
                spellButton.render(queue, alpha);
        }
    }

    @Override
    public FlexComponent getComponentAt(int xMouse, int yMouse) {
        if (!this.contains(xMouse, yMouse)) {
            return null;
        }
        SpellButton spellButton = getSpellButton(xMouse, yMouse);
        if (spellButton == null)
            return this;
        else
            return spellButton;
    }

    private SpellButton getSpellButton(int xMouse, int yMouse) {
        for (SpellButton spellButton : spellButtons)
            if (spellButton != null && spellButton.contains(xMouse, yMouse))
                return spellButton;
        return null;
    }

    @Override
    void leftPressed(int xMouse, int yMouse, int clickCount) {
        SpellButton spellButton = getSpellButton(xMouse, yMouse);
        if (spellButton == null)
            PrettySpellListMod.clearPrettySpellLists();
        else
            spellButton.leftPressed(xMouse, yMouse, clickCount);
    }

    @Override
    void rightPressed(int xMouse, int yMouse, int clickCount) {
        SpellButton spellButton = getSpellButton(xMouse, yMouse);
        if (spellButton == null)
            PrettySpellListMod.clearPrettySpellLists();
        else
            spellButton.rightPressed(xMouse, yMouse, clickCount);
    }

    public interface SpellButtonListener {
        void onClick(SpellAction spellAction);
    }
}
