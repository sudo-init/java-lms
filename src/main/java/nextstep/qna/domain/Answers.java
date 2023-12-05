package nextstep.qna.domain;

import nextstep.qna.CannotDeleteException;
import nextstep.users.domain.NsUser;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Answers {

    private List<Answer> answers;

    public Answers() {
        answers = new ArrayList<>();
    }

    public void add(Answer answer){
        answers.add(answer);
    }

    public void delete(NsUser loginUser) throws CannotDeleteException {
        for (Answer answer : answers) {
            answer.delete(loginUser);
        }
    }

    public List<DeleteHistory> deleteHistories() {
        List<DeleteHistory> deleteHistories = new ArrayList<>();
        for (Answer answer : answers) {
            deleteHistories.add(answer.deleteHistory());
        }
        return deleteHistories;
    }
}
