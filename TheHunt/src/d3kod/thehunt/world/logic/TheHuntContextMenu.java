package d3kod.thehunt.world.logic;

import java.util.ArrayList;

import com.db4o.cs.internal.messages.MNull;

import android.graphics.PointF;
import d3kod.graphics.sprite.D3Sprite;
import d3kod.graphics.sprite.SpriteManager;
import d3kod.graphics.sprite.shapes.D3Circle;
import d3kod.graphics.sprite.shapes.D3FadingText;
import d3kod.graphics.text.GLText;
import d3kod.thehunt.world.floating_text.ToolText;
import d3kod.thehunt.world.tools.CatchNet;
import d3kod.thehunt.world.tools.Tool;

public class TheHuntContextMenu extends D3Sprite {

	private static final float ELEMENT_RADIUS = 0.08f;
	private static final float MENU_ITEM_ADJ = 0.08f;
	private ArrayList<D3Sprite> menuItems;
	int change;


	public TheHuntContextMenu(SpriteManager d3gles20) {
		super(new PointF(0, 0), d3gles20);
		menuItems = new ArrayList<D3Sprite>();
		change = 0;
	}
	
	public void addTools(Tool[] tools) {
		for (int i = 0; i < tools.length; ++i) {
			menuItems.add(new D3ImageSprite(new PointF(0, 0), tools[0].toString(), getSpriteManager()));
		}
	}
	
	@Override
	public void draw(float[] vMatrix, float[] projMatrix, float interpolation) {
		// TODO Auto-generated method stub
		super.draw(vMatrix, projMatrix, interpolation);
	}

	private PointF getMenuItemPos(int i) {
		if (i > 3) {
			//TODO, can only handle 4 items
			return new PointF(0, 0);
		}
		switch(i) {
		case 0:
			return new PointF(0, MENU_ITEM_ADJ);
		case 1:
			return new PointF(MENU_ITEM_ADJ, 0);
		case 2:
			return new PointF(0, -MENU_ITEM_ADJ);
		case 3:
			return new PointF(-MENU_ITEM_ADJ, 0);
		default:
			return new PointF(0, 0);
		}
	}

	@Override
	public void initGraphic() {
		mGraphic = new D3Circle(ELEMENT_RADIUS, new float[]{0, 0, 0}, 30);
//		mGraphic = new D3t
		super.initGraphic(mGraphic);
		mGraphic.setFaded();
	}

	public void handleTouch(PointF location) {
	}

	public void hide() {
		mGraphic.setFaded();
	}

	public boolean isHidden() {
		return mGraphic.fadeDone();
	}

	public void show(PointF position) {
		setPosition(position);
		mGraphic.noFade();
		mGraphic.setPosition(position.x, position.y);
		
		SpriteManager spriteMan = getSpriteManager();
		if (change == 0) {
			change = 1;
			spriteMan.putText(new ToolText("Knife", position.x, position.y));
		}
		else {
			change = 0;
			spriteMan.putText(new ToolText("Net", position.x, position.y));
		}
	}

	public int getChange() {
		return change;
	}

}
