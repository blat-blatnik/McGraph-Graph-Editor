package controller.animation;

import utils.BoundedList;

import java.awt.*;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @author Boris
 * @version 2.0
 *
 * Animations are small snippets of code that execute 60 times per second for a fixed duration while playing. They are
 * meant to be used for moving stuff around the screen, changing colors, etc. without any user input. For example, when
 * a Node is clicked on, an Animation will play that makes the Node expand and then shrink really quickly to show the
 * user that it was selected.
 */
public class Animation {

    /**
     * The interface stores a method that will execute 60 times per second while the Animation is running.
     */
    public interface UpdateAction {
        /**
         * This method will be called 60 times per second while the Animation is running.
         *
         * @param animation The animation being updated.
         */
        void update(Animation animation);
    }

    private double currentTime;
    private final double duration;
    private final UpdateAction updateAction;

    /**
     * Constructs an Animation that lasts for a specific duration and executes a given UpdateAction every tick. The
     * Animation does not start playing immediately, so play() must be called to get it to start.
     *
     * @param duration The *positive* duration of the Animation.
     * @param updateAction The UpdateAction that is performed continuously while the Animation is running.
     */
    public Animation(double duration, UpdateAction updateAction) {
        if (duration < 0)
            throw new IllegalArgumentException("duration must be positive");

        this.currentTime = 0;
        this.duration = duration;
        this.updateAction = updateAction;
    }

    /**
     * Constructs an animation that lasts indefinitely until stop() is called.
     *
     * @param updateAction The UpdateAction that is performed continuously while the Animation is running.
     */
    public Animation(UpdateAction updateAction) {
        this(Double.POSITIVE_INFINITY, updateAction);
    }

    /**
     * Begins playing the current Animation, unless the Animation has already finished.
     */
    public void play() {
        if (!isOver() && !isPlaying()) {
            Manager.playingAnimations.add(this);
        }
    }

    /**
     * Stops the current Animation.
     */
    public void stop() {
        Manager.playingAnimations.remove(this);
    }

    /**
     * @return Whether the Animation is currently playing.
     */
    public boolean isPlaying() {
        return Manager.playingAnimations.contains(this);
    }

    /**
     * @return Whether the Animation has finished playing.
     */
    public boolean isOver() {
        return currentTime >= duration;
    }

    /**
     * @return The duration of this Animation in seconds.
     */
    public double getDuration() {
        return duration;
    }

    /**
     * @return The current time tick of this Animation in seconds.
     */
    public double getCurrentTime() {
        return currentTime;
    }

    /**
     * Sets the current time for this Animation - which can be used to reset it once it has finished, or to a previous
     * time tick, or to advance the time tick.
     *
     * @param newTime The new time tick for the Animation, clamped to [0, duration].
     */
    public void setCurrentTime(double newTime) {
        if (newTime < 0)
            newTime = 0;
        else if (newTime > duration)
            newTime = duration;

        currentTime = newTime;
    }

    /**
     * Trigger's this Animation's UpdateAction.
     */
    public void triggerUpdate() {
        updateAction.update(this);
    }

    /**
     * The Animation Manager synchronizes and schedules all of the currently playing Animations. All playing Animations
     * are scheduled in the awt Event Dispatch Thread - so Animations should be kept light-weight to avoid bogging it
     * down.
     */
    public static final class Manager {

        private static final double ANIMATION_TICKS_PER_SECOND = 60.0;
        private static final Timer animationTimer = new Timer();
        private static final BoundedList<Animation> playingAnimations = new BoundedList<>(64);
        private static long lastNanoTime;

        /**
         * Initializes the Manager and Schedules a timer that will play Animations 60 times per second.
         */
        public static void startAnimating() {
            TimerTask updateAnimations = new TimerTask() {
                @Override
                public void run() {
                    // invokeLater() will place this into the even dispatch thread.
                    EventQueue.invokeLater(() -> {
                        long nanoTime = System.nanoTime();
                        double deltaTime = (nanoTime - lastNanoTime) / 1000000000.0;

                        for (Animation animation : playingAnimations) {
                            animation.triggerUpdate();
                            animation.setCurrentTime(animation.getCurrentTime() + deltaTime);
                        }

                        playingAnimations.removeIf(Animation::isOver);

                        lastNanoTime = nanoTime;
                    });
                }
            };

            final long TIMER_PERIOD_IN_MILLISECONDS = (long)(1000.0 / ANIMATION_TICKS_PER_SECOND);
            lastNanoTime = System.nanoTime();
            animationTimer.scheduleAtFixedRate(updateAnimations, 0, TIMER_PERIOD_IN_MILLISECONDS);
        }

        /**
         * Stops all currently playing Animations.
         */
        public static void stopAnimating() {
            playingAnimations.clear();
            animationTimer.cancel();
        }

        /**
         * This class contains only static fields and methods and should never be instantiated.
         */
        private Manager() {}
    }
}