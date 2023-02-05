package hello.springtx.propagation;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.UnexpectedRollbackException;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
@SpringBootTest
class MemberServiceTest {

    @Autowired MemberService memberService;
    @Autowired MemberRepository memberRepository;
    @Autowired LogRepository logRepository;

    /**
     * memberService @Transactional: OFF
     * logService @Transactional: ON
     * logRepository @Transactional: ON
     */
    @Test
    void outerTxOff_success() {
        // given
        String username = "outerTxOff_success";

        // when
        memberService.joinV1(username);

        // then
        assertTrue(memberRepository.find(username).isPresent());
        assertTrue(logRepository.find(username).isPresent());
    }

    /**
     * memberService @Transactional: OFF
     * logService @Transactional: ON
     * logRepository @Transactional: ON Exception
     */
    @Test
    void outerTxOff_fail() {
        // given
        String username = "로그예외_outerTxOff_success";

        // when
        Assertions.assertThatThrownBy(() -> memberService.joinV1(username))
                .isInstanceOf(RuntimeException.class);

        // then
        assertTrue(memberRepository.find(username).isPresent());
        assertTrue(logRepository.find(username).isEmpty());
    }

    /**
     * memberService @Transactional: OFF
     * logService @Transactional: ON
     * logRepository @Transactional: ON
     */
    @Test
    void singleTx() {
        // given
        String username = "outerTxOff_success";

        // when
        memberService.joinV1(username);

        // then
        assertTrue(memberRepository.find(username).isPresent());
        assertTrue(logRepository.find(username).isPresent());
    }

    /**
     * memberService @Transactional: ON
     * logService @Transactional: ON
     * logRepository @Transactional: ON
     */
    @Test
    void outerTxOn_success() {
        // given
        String username = "outerTxOn_success";

        // when
        memberService.joinV1(username);

        // then
        assertTrue(memberRepository.find(username).isPresent());
        assertTrue(logRepository.find(username).isPresent());
    }

    /**
     * memberService @Transactional: ON
     * logService @Transactional: ON
     * logRepository @Transactional: ON Exception
     */
    @Test
    void outerTxOn_fail() {
        // given
        String username = "로그예외_outerTxOn_fail";

        // when
        Assertions.assertThatThrownBy(() -> memberService.joinV1(username))
                .isInstanceOf(RuntimeException.class);

        // then
        assertTrue(memberRepository.find(username).isEmpty());
        assertTrue(logRepository.find(username).isEmpty());
    }

    /**
     * memberService @Transactional: ON
     * logService @Transactional: ON
     * logRepository @Transactional: ON Exception
     */
    @Test
    void recoverException_fail() {
        // given
        String username = "로그예외_recoverException_fail";

        // when
        Assertions.assertThatThrownBy(() -> memberService.joinV2(username))
                .isInstanceOf(UnexpectedRollbackException.class);

        // then
        assertTrue(memberRepository.find(username).isEmpty());
        assertTrue(logRepository.find(username).isEmpty());
    }
}