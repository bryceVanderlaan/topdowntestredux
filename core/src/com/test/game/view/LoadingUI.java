package com.test.game.view;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.StringBuilder;
import com.test.game.TopDownTestRedux;
import com.test.game.input.GameKeys;

public class LoadingUI extends Table {
    private final String loadingString;
    private final ProgressBar progressBar;
    private final TextButton pressAnyKey;
    private final TextButton textButton;
    public LoadingUI(final TopDownTestRedux context) {
        super(context.getSkin());
        setFillParent(true);

        progressBar = new ProgressBar(0,1,0.01f,false,getSkin(),"default");
        progressBar.setAnimateDuration(1);

        loadingString = "Loading...";
        textButton = new TextButton(loadingString,getSkin(),"huge");
        textButton.getLabel().setWrap(true);


        pressAnyKey = new TextButton("Press any key to continue",getSkin(),"normal");
        pressAnyKey.getLabel().setWrap(true);
        pressAnyKey.setVisible(false);
        pressAnyKey.addListener(new InputListener() {
                                    @Override
                                    public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                                        context.getInputManager().notifyKeyDown(GameKeys.SELECT);
                                        return true;
                                    }
                                });

        add(pressAnyKey).expand().fill().top().row();
        add(textButton).expandX().fillX().bottom().row();
        add(progressBar).expandX().fillX().bottom().pad(20,25,20,25);
    }

    public void setProgress (final float progress) {
        progressBar.setValue(progress);

        final StringBuilder stringBuilder = textButton.getLabel().getText();
        stringBuilder.setLength(0);
        textButton.getLabel().invalidateHierarchy();
        stringBuilder.append(loadingString);
        stringBuilder.append(" (");
        stringBuilder.append(progress * 100);
        stringBuilder.append("%)");


        if (progress >= 1 && !pressAnyKey.isVisible()) {
            pressAnyKey.setVisible(true);
            pressAnyKey.setColor(1,1,1,0);
            pressAnyKey.addAction(Actions.forever(Actions.sequence(Actions.alpha(1,1),Actions.alpha(0,1))));
        }
    }
}
