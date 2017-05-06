package site.hanschen.pretty.db.repository;

import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;

import java.util.List;

import site.hanschen.pretty.db.bean.Picture;
import site.hanschen.pretty.db.bean.Question;

/**
 * @author HansChen
 */
@WorkerThread
public interface PrettyRepository {

    long insertOrUpdate(Question question);

    @Nullable
    Question getQuestion(int questionId);

    List<Question> getAllQuestion();

    void deleteQuestion(int questionId);

    long insertOrUpdate(Picture picture);

    List<Picture> getPictures(int questionId);

    @Nullable
    Picture getPicture(String url, int questionId);

    void deletePictures(int questionId);

    void deletePicture(String url, int questionId);
}
