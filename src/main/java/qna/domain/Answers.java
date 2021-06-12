package qna.domain;

import qna.CannotDeleteException;

import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Embeddable
public class Answers {

    @OneToMany(mappedBy = "question")
    private List<Answer> answers = new ArrayList<>();

    protected Answers() {
    }

    public Answers(List<Answer> answers) {
        this.answers = answers;
    }

    void validateOwners(User loginUser) throws CannotDeleteException {
        for (Answer answer : answers) {
            validateOwner(loginUser, answer);
        }
    }

    private void validateOwner(User loginUser, Answer answer) throws CannotDeleteException {
        if (!answer.isOwner(loginUser)) {
            throw new CannotDeleteException("다른 사람이 쓴 답변이 있어 삭제할 수 없습니다.");
        }
    }

    List<DeleteHistory> deleteAllAndAddHistories(List<DeleteHistory> deleteHistories) {
        List<DeleteHistory> newDeleteHistories = new ArrayList<>(deleteHistories);
        for (Answer answer : answers) {
            answer.setDeleted(true);
            newDeleteHistories.add(new DeleteHistory(ContentType.ANSWER, answer.getId(), answer.getWriter(), LocalDateTime.now()));
        }

        return List.copyOf(newDeleteHistories);
    }

    void add(Answer answer) {
        answers.add(answer);
    }
}