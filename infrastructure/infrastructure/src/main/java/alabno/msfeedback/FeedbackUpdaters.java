package alabno.msfeedback;

import java.util.ArrayList;
import java.util.List;

import alabno.wserver.SourceDocument;

/**
 * Contains the list of registered Microservice Feedback Updaters
 *
 */
public class FeedbackUpdaters implements MicroServiceUpdater {

    private List<MicroServiceUpdater> updaters = new ArrayList<>();
    
    public void register(MicroServiceUpdater updater) {
        updaters.add(updater);
        updater.init();
    }

    @Override
    public void init() {
        // This method does nothing
        // init is called upon registration
    }

    @Override
    public void update(SourceDocument source, int lineNumber, String type, String annotation) {
        for (MicroServiceUpdater updater : updaters) {
            updater.update(source, lineNumber, type, annotation);
        }
    }

    @Override
    public void updateMark(SourceDocument source, String exerciseType, Mark mark) {
        System.out.println("FeedbackUpdaters: received updateMark with mark " + mark.toString() + " and file " + source.getPath());
        for (MicroServiceUpdater updater : updaters) {
            updater.updateMark(source, exerciseType, mark);
        }
    }

}
