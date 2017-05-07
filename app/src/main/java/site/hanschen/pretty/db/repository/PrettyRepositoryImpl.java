package site.hanschen.pretty.db.repository;

import android.support.annotation.Nullable;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import site.hanschen.pretty.db.bean.Picture;
import site.hanschen.pretty.db.bean.Question;
import site.hanschen.pretty.db.gen.PictureDao;
import site.hanschen.pretty.db.gen.QuestionDao;

/**
 * @author HansChen
 */

public class PrettyRepositoryImpl implements PrettyRepository {

    private PictureDao  mPictureDao;
    private QuestionDao mQuestionDao;

    public PrettyRepositoryImpl(PictureDao pictureDao, QuestionDao questionDao) {
        mPictureDao = pictureDao;
        mQuestionDao = questionDao;
    }

    @Override
    public long insertOrUpdate(Question question) {
        Question old = getQuestion(question.getQuestionId());
        if (old == null) {
            return mQuestionDao.insert(question);
        } else {
            if (!old.equals(question)) {
                old.setQuestionId(question.getQuestionId());
                old.setTitle(question.getTitle());
                old.setAnswerCount(question.getAnswerCount());
                mQuestionDao.update(old);
            }
            return old.getId();
        }
    }

    @Override
    @Nullable
    public Question getQuestion(int questionId) {
        return mQuestionDao.queryBuilder().where(QuestionDao.Properties.QuestionId.eq(questionId)).build().unique();
    }

    @Override
    public List<Question> getAllQuestion() {
        return mQuestionDao.loadAll();
    }

    @Override
    public void deleteQuestion(int questionId) {
        Question question = getQuestion(questionId);
        List<Picture> pictures = getPictures(questionId);
        mPictureDao.deleteInTx(pictures);
        mQuestionDao.delete(question);
    }

    @Override
    public long insertOrUpdate(Picture picture) {
        Picture old = getPicture(picture.getUrl(), picture.getQuestionId());
        if (old == null) {
            return mPictureDao.insert(picture);
        } else {
            if (!old.equals(picture)) {
                old.setQuestionId(picture.getQuestionId());
                old.setUrl(picture.getUrl());
                mPictureDao.update(old);
            }
            return old.getId();
        }
    }

    @Override
    public List<Picture> getPictures(int questionId) {
        Question question = getQuestion(questionId);
        return question == null ? new ArrayList<Picture>() : question.getPictures();
    }

    @Override
    @Nullable
    public Picture getPicture(String url, int questionId) {
        return mPictureDao.queryBuilder()
                          .where(PictureDao.Properties.Url.eq(url), PictureDao.Properties.QuestionId.eq(questionId))
                          .build()
                          .unique();
    }

    @Override
    public void deletePictures(int questionId) {
        List<Picture> pictures = getPictures(questionId);
        mPictureDao.deleteInTx(pictures);
    }

    @Override
    public void deletePicture(String url, int questionId) {
        Picture picture = getPicture(url, questionId);
        mPictureDao.delete(picture);
    }
}
