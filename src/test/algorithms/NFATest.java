package algorithms;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static algorithms.Utility.makeNFA;
import static org.junit.jupiter.api.Assertions.assertEquals;

class NFATest {

    @BeforeEach
    void setUp() {
        State.setIdCounter(0);
    }

    @Test
    void regexToNFABasic() {
        NFA expected = makeNFA(4);
        Utility.addSymbols(expected, 'a', 'b');
        Utility.addStates(expected, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11);
        Utility.addFinalStates(expected, 11);
        Utility.addMoves(expected,
                Utility.makeMove(0, 'a', 1),
                Utility.makeMove(1, NFA.EPSILON, 5),
                Utility.makeMove(2, 'b', 3),
                Utility.makeMove(3, NFA.EPSILON, 5),
                Utility.makeMove(4, NFA.EPSILON, 0),
                Utility.makeMove(4, NFA.EPSILON, 2),
                Utility.makeMove(5, NFA.EPSILON, 8),
                Utility.makeMove(6, 'a', 7),
                Utility.makeMove(7, NFA.EPSILON, 6),
                Utility.makeMove(7, NFA.EPSILON, 9),
                Utility.makeMove(8, NFA.EPSILON, 6),
                Utility.makeMove(8, NFA.EPSILON, 9),
                Utility.makeMove(9, NFA.EPSILON, 10),
                Utility.makeMove(10, 'b', 11));
        NFA actual = NFA.regexToNFA("(a|b)a*b");
        assertEquals(expected, actual);
    }

    @Test
    void regexToNFAEmpty() {
        NFA expected = makeNFA(0);
        Utility.addSymbols(expected, NFA.EPSILON);
        Utility.addStates(expected, 0, 1);
        Utility.addFinalStates(expected, 1);
        Utility.addMoves(expected, Utility.makeMove(0, NFA.EPSILON, 1));
        NFA actual = NFA.regexToNFA("");
        assertEquals(expected, actual);
    }

    @Test
    void regexToNFAIdentifiers() {
        NFA expected = makeNFA(108);
        Utility.addSymbols(expected,
                '$', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                '_', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j',
                'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u',
                'v', 'w', 'x', 'y', 'z'
        );
        Utility.addStates(expected,
                0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14,
                15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29,
                30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44,
                45, 46, 47, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 58, 59,
                60, 61, 62, 63, 64, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74,
                75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89,
                90, 91, 92, 93, 94, 95, 96, 97, 98, 99, 100, 101, 102, 103, 104,
                105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 119,
                120, 121, 122, 123, 124, 125, 126, 127, 128, 129, 130, 131, 132, 133, 134,
                135, 136, 137, 138, 139, 140, 141, 142, 143, 144, 145, 146, 147, 148, 149,
                150, 151, 152, 153, 154, 155, 156, 157, 158, 159, 160, 161, 162, 163, 164,
                165, 166, 167, 168, 169, 170, 171, 172, 173, 174, 175, 176, 177, 178, 179,
                180, 181, 182, 183, 184, 185, 186, 187, 188, 189, 190, 191, 192, 193, 194,
                195, 196, 197, 198, 199, 200, 201, 202, 203, 204, 205, 206, 207, 208, 209,
                210, 211, 212, 213, 214, 215, 216, 217, 218, 219, 220, 221, 222, 223, 224,
                225, 226, 227, 228, 229, 230, 231, 232, 233, 234, 235, 236, 237, 238, 239,
                240, 241, 242, 243, 244, 245, 246, 247, 248, 249, 250, 251, 252, 253
        );
        Utility.addFinalStates(expected, 253);
        Utility.addMoves(expected,
                Utility.makeMove(0, '$', 1),
                Utility.makeMove(1, NFA.EPSILON, 109),
                Utility.makeMove(2, '_', 3),
                Utility.makeMove(3, NFA.EPSILON, 107),
                Utility.makeMove(4, 'a', 5),
                Utility.makeMove(5, NFA.EPSILON, 105),
                Utility.makeMove(6, 'b', 7),
                Utility.makeMove(7, NFA.EPSILON, 103),
                Utility.makeMove(8, 'c', 9),
                Utility.makeMove(9, NFA.EPSILON, 101),
                Utility.makeMove(10, 'd', 11),
                Utility.makeMove(11, NFA.EPSILON, 99),
                Utility.makeMove(12, 'e', 13),
                Utility.makeMove(13, NFA.EPSILON, 97),
                Utility.makeMove(14, 'f', 15),
                Utility.makeMove(15, NFA.EPSILON, 95),
                Utility.makeMove(16, 'g', 17),
                Utility.makeMove(17, NFA.EPSILON, 93),
                Utility.makeMove(18, 'h', 19),
                Utility.makeMove(19, NFA.EPSILON, 91),
                Utility.makeMove(20, 'i', 21),
                Utility.makeMove(21, NFA.EPSILON, 89),
                Utility.makeMove(22, 'j', 23),
                Utility.makeMove(23, NFA.EPSILON, 87),
                Utility.makeMove(24, 'k', 25),
                Utility.makeMove(25, NFA.EPSILON, 85),
                Utility.makeMove(26, 'l', 27),
                Utility.makeMove(27, NFA.EPSILON, 83),
                Utility.makeMove(28, 'm', 29),
                Utility.makeMove(29, NFA.EPSILON, 81),
                Utility.makeMove(30, 'n', 31),
                Utility.makeMove(31, NFA.EPSILON, 79),
                Utility.makeMove(32, 'o', 33),
                Utility.makeMove(33, NFA.EPSILON, 77),
                Utility.makeMove(34, 'p', 35),
                Utility.makeMove(35, NFA.EPSILON, 75),
                Utility.makeMove(36, 'q', 37),
                Utility.makeMove(37, NFA.EPSILON, 73),
                Utility.makeMove(38, 'r', 39),
                Utility.makeMove(39, NFA.EPSILON, 71),
                Utility.makeMove(40, 's', 41),
                Utility.makeMove(41, NFA.EPSILON, 69),
                Utility.makeMove(42, 't', 43),
                Utility.makeMove(43, NFA.EPSILON, 67),
                Utility.makeMove(44, 'u', 45),
                Utility.makeMove(45, NFA.EPSILON, 65),
                Utility.makeMove(46, 'v', 47),
                Utility.makeMove(47, NFA.EPSILON, 63),
                Utility.makeMove(48, 'w', 49),
                Utility.makeMove(49, NFA.EPSILON, 61),
                Utility.makeMove(50, 'x', 51),
                Utility.makeMove(51, NFA.EPSILON, 59),
                Utility.makeMove(52, 'y', 53),
                Utility.makeMove(53, NFA.EPSILON, 57),
                Utility.makeMove(54, 'z', 55),
                Utility.makeMove(55, NFA.EPSILON, 57),
                Utility.makeMove(56, NFA.EPSILON, 52),
                Utility.makeMove(56, NFA.EPSILON, 54),
                Utility.makeMove(57, NFA.EPSILON, 59),
                Utility.makeMove(58, NFA.EPSILON, 50),
                Utility.makeMove(58, NFA.EPSILON, 56),
                Utility.makeMove(59, NFA.EPSILON, 61),
                Utility.makeMove(60, NFA.EPSILON, 48),
                Utility.makeMove(60, NFA.EPSILON, 58),
                Utility.makeMove(61, NFA.EPSILON, 63),
                Utility.makeMove(62, NFA.EPSILON, 46),
                Utility.makeMove(62, NFA.EPSILON, 60),
                Utility.makeMove(63, NFA.EPSILON, 65),
                Utility.makeMove(64, NFA.EPSILON, 44),
                Utility.makeMove(64, NFA.EPSILON, 62),
                Utility.makeMove(65, NFA.EPSILON, 67),
                Utility.makeMove(66, NFA.EPSILON, 42),
                Utility.makeMove(66, NFA.EPSILON, 64),
                Utility.makeMove(67, NFA.EPSILON, 69),
                Utility.makeMove(68, NFA.EPSILON, 40),
                Utility.makeMove(68, NFA.EPSILON, 66),
                Utility.makeMove(69, NFA.EPSILON, 71),
                Utility.makeMove(70, NFA.EPSILON, 38),
                Utility.makeMove(70, NFA.EPSILON, 68),
                Utility.makeMove(71, NFA.EPSILON, 73),
                Utility.makeMove(72, NFA.EPSILON, 36),
                Utility.makeMove(72, NFA.EPSILON, 70),
                Utility.makeMove(73, NFA.EPSILON, 75),
                Utility.makeMove(74, NFA.EPSILON, 34),
                Utility.makeMove(74, NFA.EPSILON, 72),
                Utility.makeMove(75, NFA.EPSILON, 77),
                Utility.makeMove(76, NFA.EPSILON, 32),
                Utility.makeMove(76, NFA.EPSILON, 74),
                Utility.makeMove(77, NFA.EPSILON, 79),
                Utility.makeMove(78, NFA.EPSILON, 30),
                Utility.makeMove(78, NFA.EPSILON, 76),
                Utility.makeMove(79, NFA.EPSILON, 81),
                Utility.makeMove(80, NFA.EPSILON, 28),
                Utility.makeMove(80, NFA.EPSILON, 78),
                Utility.makeMove(81, NFA.EPSILON, 83),
                Utility.makeMove(82, NFA.EPSILON, 26),
                Utility.makeMove(82, NFA.EPSILON, 80),
                Utility.makeMove(83, NFA.EPSILON, 85),
                Utility.makeMove(84, NFA.EPSILON, 24),
                Utility.makeMove(84, NFA.EPSILON, 82),
                Utility.makeMove(85, NFA.EPSILON, 87),
                Utility.makeMove(86, NFA.EPSILON, 22),
                Utility.makeMove(86, NFA.EPSILON, 84),
                Utility.makeMove(87, NFA.EPSILON, 89),
                Utility.makeMove(88, NFA.EPSILON, 20),
                Utility.makeMove(88, NFA.EPSILON, 86),
                Utility.makeMove(89, NFA.EPSILON, 91),
                Utility.makeMove(90, NFA.EPSILON, 18),
                Utility.makeMove(90, NFA.EPSILON, 88),
                Utility.makeMove(91, NFA.EPSILON, 93),
                Utility.makeMove(92, NFA.EPSILON, 16),
                Utility.makeMove(92, NFA.EPSILON, 90),
                Utility.makeMove(93, NFA.EPSILON, 95),
                Utility.makeMove(94, NFA.EPSILON, 14),
                Utility.makeMove(94, NFA.EPSILON, 92),
                Utility.makeMove(95, NFA.EPSILON, 97),
                Utility.makeMove(96, NFA.EPSILON, 12),
                Utility.makeMove(96, NFA.EPSILON, 94),
                Utility.makeMove(97, NFA.EPSILON, 99),
                Utility.makeMove(98, NFA.EPSILON, 10),
                Utility.makeMove(98, NFA.EPSILON, 96),
                Utility.makeMove(99, NFA.EPSILON, 101),
                Utility.makeMove(100, NFA.EPSILON, 8),
                Utility.makeMove(100, NFA.EPSILON, 98),
                Utility.makeMove(101, NFA.EPSILON, 103),
                Utility.makeMove(102, NFA.EPSILON, 6),
                Utility.makeMove(102, NFA.EPSILON, 100),
                Utility.makeMove(103, NFA.EPSILON, 105),
                Utility.makeMove(104, NFA.EPSILON, 4),
                Utility.makeMove(104, NFA.EPSILON, 102),
                Utility.makeMove(105, NFA.EPSILON, 107),
                Utility.makeMove(106, NFA.EPSILON, 2),
                Utility.makeMove(106, NFA.EPSILON, 104),
                Utility.makeMove(107, NFA.EPSILON, 109),
                Utility.makeMove(108, NFA.EPSILON, 0),
                Utility.makeMove(108, NFA.EPSILON, 106),
                Utility.makeMove(109, NFA.EPSILON, 252),
                Utility.makeMove(110, 'a', 111),
                Utility.makeMove(111, NFA.EPSILON, 251),
                Utility.makeMove(112, 'b', 113),
                Utility.makeMove(113, NFA.EPSILON, 249),
                Utility.makeMove(114, 'c', 115),
                Utility.makeMove(115, NFA.EPSILON, 247),
                Utility.makeMove(116, 'd', 117),
                Utility.makeMove(117, NFA.EPSILON, 245),
                Utility.makeMove(118, 'e', 119),
                Utility.makeMove(119, NFA.EPSILON, 243),
                Utility.makeMove(120, 'f', 121),
                Utility.makeMove(121, NFA.EPSILON, 241),
                Utility.makeMove(122, 'g', 123),
                Utility.makeMove(123, NFA.EPSILON, 239),
                Utility.makeMove(124, 'h', 125),
                Utility.makeMove(125, NFA.EPSILON, 237),
                Utility.makeMove(126, 'i', 127),
                Utility.makeMove(127, NFA.EPSILON, 235),
                Utility.makeMove(128, 'j', 129),
                Utility.makeMove(129, NFA.EPSILON, 233),
                Utility.makeMove(130, 'k', 131),
                Utility.makeMove(131, NFA.EPSILON, 231),
                Utility.makeMove(132, 'l', 133),
                Utility.makeMove(133, NFA.EPSILON, 229),
                Utility.makeMove(134, 'm', 135),
                Utility.makeMove(135, NFA.EPSILON, 227),
                Utility.makeMove(136, 'n', 137),
                Utility.makeMove(137, NFA.EPSILON, 225),
                Utility.makeMove(138, 'o', 139),
                Utility.makeMove(139, NFA.EPSILON, 223),
                Utility.makeMove(140, 'p', 141),
                Utility.makeMove(141, NFA.EPSILON, 221),
                Utility.makeMove(142, 'q', 143),
                Utility.makeMove(143, NFA.EPSILON, 219),
                Utility.makeMove(144, 'r', 145),
                Utility.makeMove(145, NFA.EPSILON, 217),
                Utility.makeMove(146, 's', 147),
                Utility.makeMove(147, NFA.EPSILON, 215),
                Utility.makeMove(148, 't', 149),
                Utility.makeMove(149, NFA.EPSILON, 213),
                Utility.makeMove(150, 'u', 151),
                Utility.makeMove(151, NFA.EPSILON, 211),
                Utility.makeMove(152, 'v', 153),
                Utility.makeMove(153, NFA.EPSILON, 209),
                Utility.makeMove(154, 'w', 155),
                Utility.makeMove(155, NFA.EPSILON, 207),
                Utility.makeMove(156, 'x', 157),
                Utility.makeMove(157, NFA.EPSILON, 205),
                Utility.makeMove(158, 'y', 159),
                Utility.makeMove(159, NFA.EPSILON, 203),
                Utility.makeMove(160, 'z', 161),
                Utility.makeMove(161, NFA.EPSILON, 201),
                Utility.makeMove(162, '0', 163),
                Utility.makeMove(163, NFA.EPSILON, 199),
                Utility.makeMove(164, '1', 165),
                Utility.makeMove(165, NFA.EPSILON, 197),
                Utility.makeMove(166, '2', 167),
                Utility.makeMove(167, NFA.EPSILON, 195),
                Utility.makeMove(168, '3', 169),
                Utility.makeMove(169, NFA.EPSILON, 193),
                Utility.makeMove(170, '4', 171),
                Utility.makeMove(171, NFA.EPSILON, 191),
                Utility.makeMove(172, '5', 173),
                Utility.makeMove(173, NFA.EPSILON, 189),
                Utility.makeMove(174, '6', 175),
                Utility.makeMove(175, NFA.EPSILON, 187),
                Utility.makeMove(176, '7', 177),
                Utility.makeMove(177, NFA.EPSILON, 185),
                Utility.makeMove(178, '8', 179),
                Utility.makeMove(179, NFA.EPSILON, 183),
                Utility.makeMove(180, '9', 181),
                Utility.makeMove(181, NFA.EPSILON, 183),
                Utility.makeMove(182, NFA.EPSILON, 178),
                Utility.makeMove(182, NFA.EPSILON, 180),
                Utility.makeMove(183, NFA.EPSILON, 185),
                Utility.makeMove(184, NFA.EPSILON, 176),
                Utility.makeMove(184, NFA.EPSILON, 182),
                Utility.makeMove(185, NFA.EPSILON, 187),
                Utility.makeMove(186, NFA.EPSILON, 174),
                Utility.makeMove(186, NFA.EPSILON, 184),
                Utility.makeMove(187, NFA.EPSILON, 189),
                Utility.makeMove(188, NFA.EPSILON, 172),
                Utility.makeMove(188, NFA.EPSILON, 186),
                Utility.makeMove(189, NFA.EPSILON, 191),
                Utility.makeMove(190, NFA.EPSILON, 170),
                Utility.makeMove(190, NFA.EPSILON, 188),
                Utility.makeMove(191, NFA.EPSILON, 193),
                Utility.makeMove(192, NFA.EPSILON, 168),
                Utility.makeMove(192, NFA.EPSILON, 190),
                Utility.makeMove(193, NFA.EPSILON, 195),
                Utility.makeMove(194, NFA.EPSILON, 166),
                Utility.makeMove(194, NFA.EPSILON, 192),
                Utility.makeMove(195, NFA.EPSILON, 197),
                Utility.makeMove(196, NFA.EPSILON, 164),
                Utility.makeMove(196, NFA.EPSILON, 194),
                Utility.makeMove(197, NFA.EPSILON, 199),
                Utility.makeMove(198, NFA.EPSILON, 162),
                Utility.makeMove(198, NFA.EPSILON, 196),
                Utility.makeMove(199, NFA.EPSILON, 201),
                Utility.makeMove(200, NFA.EPSILON, 160),
                Utility.makeMove(200, NFA.EPSILON, 198),
                Utility.makeMove(201, NFA.EPSILON, 203),
                Utility.makeMove(202, NFA.EPSILON, 158),
                Utility.makeMove(202, NFA.EPSILON, 200),
                Utility.makeMove(203, NFA.EPSILON, 205),
                Utility.makeMove(204, NFA.EPSILON, 156),
                Utility.makeMove(204, NFA.EPSILON, 202),
                Utility.makeMove(205, NFA.EPSILON, 207),
                Utility.makeMove(206, NFA.EPSILON, 154),
                Utility.makeMove(206, NFA.EPSILON, 204),
                Utility.makeMove(207, NFA.EPSILON, 209),
                Utility.makeMove(208, NFA.EPSILON, 152),
                Utility.makeMove(208, NFA.EPSILON, 206),
                Utility.makeMove(209, NFA.EPSILON, 211),
                Utility.makeMove(210, NFA.EPSILON, 150),
                Utility.makeMove(210, NFA.EPSILON, 208),
                Utility.makeMove(211, NFA.EPSILON, 213),
                Utility.makeMove(212, NFA.EPSILON, 148),
                Utility.makeMove(212, NFA.EPSILON, 210),
                Utility.makeMove(213, NFA.EPSILON, 215),
                Utility.makeMove(214, NFA.EPSILON, 146),
                Utility.makeMove(214, NFA.EPSILON, 212),
                Utility.makeMove(215, NFA.EPSILON, 217),
                Utility.makeMove(216, NFA.EPSILON, 144),
                Utility.makeMove(216, NFA.EPSILON, 214),
                Utility.makeMove(217, NFA.EPSILON, 219),
                Utility.makeMove(218, NFA.EPSILON, 142),
                Utility.makeMove(218, NFA.EPSILON, 216),
                Utility.makeMove(219, NFA.EPSILON, 221),
                Utility.makeMove(220, NFA.EPSILON, 140),
                Utility.makeMove(220, NFA.EPSILON, 218),
                Utility.makeMove(221, NFA.EPSILON, 223),
                Utility.makeMove(222, NFA.EPSILON, 138),
                Utility.makeMove(222, NFA.EPSILON, 220),
                Utility.makeMove(223, NFA.EPSILON, 225),
                Utility.makeMove(224, NFA.EPSILON, 136),
                Utility.makeMove(224, NFA.EPSILON, 222),
                Utility.makeMove(225, NFA.EPSILON, 227),
                Utility.makeMove(226, NFA.EPSILON, 134),
                Utility.makeMove(226, NFA.EPSILON, 224),
                Utility.makeMove(227, NFA.EPSILON, 229),
                Utility.makeMove(228, NFA.EPSILON, 132),
                Utility.makeMove(228, NFA.EPSILON, 226),
                Utility.makeMove(229, NFA.EPSILON, 231),
                Utility.makeMove(230, NFA.EPSILON, 130),
                Utility.makeMove(230, NFA.EPSILON, 228),
                Utility.makeMove(231, NFA.EPSILON, 233),
                Utility.makeMove(232, NFA.EPSILON, 128),
                Utility.makeMove(232, NFA.EPSILON, 230),
                Utility.makeMove(233, NFA.EPSILON, 235),
                Utility.makeMove(234, NFA.EPSILON, 126),
                Utility.makeMove(234, NFA.EPSILON, 232),
                Utility.makeMove(235, NFA.EPSILON, 237),
                Utility.makeMove(236, NFA.EPSILON, 124),
                Utility.makeMove(236, NFA.EPSILON, 234),
                Utility.makeMove(237, NFA.EPSILON, 239),
                Utility.makeMove(238, NFA.EPSILON, 122),
                Utility.makeMove(238, NFA.EPSILON, 236),
                Utility.makeMove(239, NFA.EPSILON, 241),
                Utility.makeMove(240, NFA.EPSILON, 120),
                Utility.makeMove(240, NFA.EPSILON, 238),
                Utility.makeMove(241, NFA.EPSILON, 243),
                Utility.makeMove(242, NFA.EPSILON, 118),
                Utility.makeMove(242, NFA.EPSILON, 240),
                Utility.makeMove(243, NFA.EPSILON, 245),
                Utility.makeMove(244, NFA.EPSILON, 116),
                Utility.makeMove(244, NFA.EPSILON, 242),
                Utility.makeMove(245, NFA.EPSILON, 247),
                Utility.makeMove(246, NFA.EPSILON, 114),
                Utility.makeMove(246, NFA.EPSILON, 244),
                Utility.makeMove(247, NFA.EPSILON, 249),
                Utility.makeMove(248, NFA.EPSILON, 112),
                Utility.makeMove(248, NFA.EPSILON, 246),
                Utility.makeMove(249, NFA.EPSILON, 251),
                Utility.makeMove(250, NFA.EPSILON, 110),
                Utility.makeMove(250, NFA.EPSILON, 248),
                Utility.makeMove(251, NFA.EPSILON, 250),
                Utility.makeMove(251, NFA.EPSILON, 253),
                Utility.makeMove(252, NFA.EPSILON, 250),
                Utility.makeMove(252, NFA.EPSILON, 253)
        );
        NFA actual = NFA.regexToNFA("($|_|a|b|c|d|e|f|g|h|i|j|k|l|m|n|o|p|q|r|s|t|u|v|w|x|y|z)" +
                "(a|b|c|d|e|f|g|h|i|j|k|l|m|n|o|p|q|r|s|t|u|v|w|x|y|z|0|1|2|3|4|5|6|7|8|9)*");
        assertEquals(expected, actual);
    }

    @Test
    void regexToNFAIntegers() {
        NFA expected = makeNFA(76);
        Utility.addSymbols(expected, '0', '1', '2', '3', '4', '5', '6', '7', '8', '9');
        Utility.addStates(expected,
                0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14,
                15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29,
                30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44,
                45, 46, 47, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 58, 59,
                60, 61, 62, 63, 64, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74,
                75, 76, 77);
        Utility.addFinalStates(expected, 77);
        Utility.addMoves(expected,
                Utility.makeMove(0, '0', 1),
                Utility.makeMove(1, NFA.EPSILON, 77),
                Utility.makeMove(2, '1', 3),
                Utility.makeMove(3, NFA.EPSILON, 35),
                Utility.makeMove(4, '2', 5),
                Utility.makeMove(5, NFA.EPSILON, 33),
                Utility.makeMove(6, '3', 7),
                Utility.makeMove(7, NFA.EPSILON, 31),
                Utility.makeMove(8, '4', 9),
                Utility.makeMove(9, NFA.EPSILON, 29),
                Utility.makeMove(10, '5', 11),
                Utility.makeMove(11, NFA.EPSILON, 27),
                Utility.makeMove(12, '6', 13),
                Utility.makeMove(13, NFA.EPSILON, 25),
                Utility.makeMove(14, '7', 15),
                Utility.makeMove(15, NFA.EPSILON, 23),
                Utility.makeMove(16, '8', 17),
                Utility.makeMove(17, NFA.EPSILON, 21),
                Utility.makeMove(18, '9', 19),
                Utility.makeMove(19, NFA.EPSILON, 21),
                Utility.makeMove(20, NFA.EPSILON, 16),
                Utility.makeMove(20, NFA.EPSILON, 18),
                Utility.makeMove(21, NFA.EPSILON, 23),
                Utility.makeMove(22, NFA.EPSILON, 14),
                Utility.makeMove(22, NFA.EPSILON, 20),
                Utility.makeMove(23, NFA.EPSILON, 25),
                Utility.makeMove(24, NFA.EPSILON, 12),
                Utility.makeMove(24, NFA.EPSILON, 22),
                Utility.makeMove(25, NFA.EPSILON, 27),
                Utility.makeMove(26, NFA.EPSILON, 10),
                Utility.makeMove(26, NFA.EPSILON, 24),
                Utility.makeMove(27, NFA.EPSILON, 29),
                Utility.makeMove(28, NFA.EPSILON, 8),
                Utility.makeMove(28, NFA.EPSILON, 26),
                Utility.makeMove(29, NFA.EPSILON, 31),
                Utility.makeMove(30, NFA.EPSILON, 6),
                Utility.makeMove(30, NFA.EPSILON, 28),
                Utility.makeMove(31, NFA.EPSILON, 33),
                Utility.makeMove(32, NFA.EPSILON, 4),
                Utility.makeMove(32, NFA.EPSILON, 30),
                Utility.makeMove(33, NFA.EPSILON, 35),
                Utility.makeMove(34, NFA.EPSILON, 2),
                Utility.makeMove(34, NFA.EPSILON, 32),
                Utility.makeMove(35, NFA.EPSILON, 74),
                Utility.makeMove(36, '0', 37),
                Utility.makeMove(37, NFA.EPSILON, 73),
                Utility.makeMove(38, '1', 39),
                Utility.makeMove(39, NFA.EPSILON, 71),
                Utility.makeMove(40, '2', 41),
                Utility.makeMove(41, NFA.EPSILON, 69),
                Utility.makeMove(42, '3', 43),
                Utility.makeMove(43, NFA.EPSILON, 67),
                Utility.makeMove(44, '4', 45),
                Utility.makeMove(45, NFA.EPSILON, 65),
                Utility.makeMove(46, '5', 47),
                Utility.makeMove(47, NFA.EPSILON, 63),
                Utility.makeMove(48, '6', 49),
                Utility.makeMove(49, NFA.EPSILON, 61),
                Utility.makeMove(50, '7', 51),
                Utility.makeMove(51, NFA.EPSILON, 59),
                Utility.makeMove(52, '8', 53),
                Utility.makeMove(53, NFA.EPSILON, 57),
                Utility.makeMove(54, '9', 55),
                Utility.makeMove(55, NFA.EPSILON, 57),
                Utility.makeMove(56, NFA.EPSILON, 52),
                Utility.makeMove(56, NFA.EPSILON, 54),
                Utility.makeMove(57, NFA.EPSILON, 59),
                Utility.makeMove(58, NFA.EPSILON, 50),
                Utility.makeMove(58, NFA.EPSILON, 56),
                Utility.makeMove(59, NFA.EPSILON, 61),
                Utility.makeMove(60, NFA.EPSILON, 48),
                Utility.makeMove(60, NFA.EPSILON, 58),
                Utility.makeMove(61, NFA.EPSILON, 63),
                Utility.makeMove(62, NFA.EPSILON, 46),
                Utility.makeMove(62, NFA.EPSILON, 60),
                Utility.makeMove(63, NFA.EPSILON, 65),
                Utility.makeMove(64, NFA.EPSILON, 44),
                Utility.makeMove(64, NFA.EPSILON, 62),
                Utility.makeMove(65, NFA.EPSILON, 67),
                Utility.makeMove(66, NFA.EPSILON, 42),
                Utility.makeMove(66, NFA.EPSILON, 64),
                Utility.makeMove(67, NFA.EPSILON, 69),
                Utility.makeMove(68, NFA.EPSILON, 40),
                Utility.makeMove(68, NFA.EPSILON, 66),
                Utility.makeMove(69, NFA.EPSILON, 71),
                Utility.makeMove(70, NFA.EPSILON, 38),
                Utility.makeMove(70, NFA.EPSILON, 68),
                Utility.makeMove(71, NFA.EPSILON, 73),
                Utility.makeMove(72, NFA.EPSILON, 36),
                Utility.makeMove(72, NFA.EPSILON, 70),
                Utility.makeMove(73, NFA.EPSILON, 72),
                Utility.makeMove(73, NFA.EPSILON, 75),
                Utility.makeMove(74, NFA.EPSILON, 72),
                Utility.makeMove(74, NFA.EPSILON, 75),
                Utility.makeMove(75, NFA.EPSILON, 77),
                Utility.makeMove(76, NFA.EPSILON, 0),
                Utility.makeMove(76, NFA.EPSILON, 34));
        NFA actual = NFA.regexToNFA("0|((1|2|3|4|5|6|7|8|9)(0|1|2|3|4|5|6|7|8|9)*)");
        assertEquals(expected, actual);
    }

    @Test
    void makeSingle() {
        NFA expected = makeNFA(0);
        Utility.addSymbols(expected, 'a');
        Utility.addStates(expected, 0, 1);
        Utility.addFinalStates(expected, 1);
        Utility.addMoves(expected, Utility.makeMove(0, 'a', 1));

        NFA actual = NFA.makeSingle('a');
        assertEquals(expected, actual);
    }

    @Test
    void concatenate() {
        NFA expected = makeNFA(0);
        Utility.addSymbols(expected, 'a', 'b');
        Utility.addStates(expected, 0, 1, 2, 3);
        Utility.addFinalStates(expected, 3);
        Utility.addMoves(expected,
                Utility.makeMove(0, 'a', 1),
                Utility.makeMove(1, NFA.EPSILON, 2),
                Utility.makeMove(2, 'b', 3));

        NFA first = makeNFA(0);
        Utility.addSymbols(first, 'a');
        Utility.addStates(first, 0, 1);
        Utility.addFinalStates(first, 1);
        Utility.addMoves(first, Utility.makeMove(0, 'a', 1));

        NFA second = makeNFA(2);
        Utility.addSymbols(second, 'b');
        Utility.addStates(second, 2, 3);
        Utility.addFinalStates(second, 3);
        Utility.addMoves(second, Utility.makeMove(2, 'b', 3));
        State.setIdCounter(4);

        NFA actual = NFA.concatenate(first, second);
        assertEquals(expected, actual);
    }

    @Test
    void kleeneStar() {
        NFA expected = makeNFA(2);
        Utility.addSymbols(expected, 'a');
        Utility.addStates(expected, 0, 1, 2, 3);
        Utility.addFinalStates(expected, 3);
        Utility.addMoves(expected,
                Utility.makeMove(0, 'a', 1),
                Utility.makeMove(1, NFA.EPSILON, 0),
                Utility.makeMove(1, NFA.EPSILON, 3),
                Utility.makeMove(2, NFA.EPSILON, 0),
                Utility.makeMove(2, NFA.EPSILON, 3));

        NFA actual = makeNFA(0);
        Utility.addSymbols(actual, 'a');
        Utility.addStates(actual, 0, 1);
        Utility.addFinalStates(actual, 1);
        Utility.addMoves(actual, Utility.makeMove(0, 'a', 1));
        State.setIdCounter(2);

        actual = NFA.kleeneStar(actual);
        assertEquals(expected, actual);
    }

    @Test
    void alternate() {
        NFA expected = makeNFA(4);
        Utility.addSymbols(expected, 'a', 'b');
        Utility.addStates(expected, 0, 1, 2, 3, 4, 5);
        Utility.addFinalStates(expected, 5);
        Utility.addMoves(expected,
                Utility.makeMove(0, 'a', 1),
                Utility.makeMove(1, NFA.EPSILON, 5),
                Utility.makeMove(2, 'b', 3),
                Utility.makeMove(3, NFA.EPSILON, 5),
                Utility.makeMove(4, NFA.EPSILON, 0),
                Utility.makeMove(4, NFA.EPSILON, 2));

        NFA first = makeNFA(0);
        Utility.addSymbols(first, 'a');
        Utility.addStates(first, 0, 1);
        Utility.addFinalStates(first, 1);
        Utility.addMoves(first, Utility.makeMove(0, 'a', 1));

        NFA second = makeNFA(2);
        Utility.addSymbols(second, 'b');
        Utility.addStates(second, 2, 3);
        Utility.addFinalStates(second, 3);
        Utility.addMoves(second, Utility.makeMove(2, 'b', 3));
        State.setIdCounter(4);

        NFA actual = NFA.alternate(first, second);
        assertEquals(expected, actual);
    }
}