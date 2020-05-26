//package co.sample.kafka.dummy;
//
//import org.assertj.core.api.Assertions;
//import org.junit.jupiter.api.*;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import java.util.function.Supplier;
//
///**
// * Example dummy unit test with Junit 5.
// *
// */
//@ExtendWith(MockitoExtension.class)
//@DisplayName("Dummy unit test")
//// TODO: Remove me.
//public class DummyUTest {
//
//    @Mock
//    private Supplier<Integer> dummyMock;
//
//    @BeforeEach
//    void setUp() {
//
//        System.out.println(">> Hello");
//    }
//
//    @AfterEach
//    void tearDown() {
//
//        System.out.println(">> Good bye");
//    }
//
//    @Test
//    @DisplayName("Dummy outer test")
//    void dummyOuterTest() {
//
//        Assertions.assertThat(dummyMock).isNotNull();
//    }
//
//    @Nested
//    @DisplayName("Group 1 of dummy tests")
//    class GroupOfDummies_1 {
//
//        @Test
//        @DisplayName("Test in group 1")
//        void groupDummyTest() {
//
//            Assertions.assertThat(dummyMock).isNotNull();
//        }
//    }
//
//    @Nested
//    @DisplayName("Group 2 of dummy tests")
//    class GroupOfDummies_2 {
//
//        @Test
//        @DisplayName("Test in group 2")
//        void groupDummyTest() {
//
//            Assertions.assertThat(dummyMock).isNotNull();
//        }
//    }
//}
