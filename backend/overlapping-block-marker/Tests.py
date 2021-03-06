
import MLUtils
from CategoryConverter import CategoryConverter
from Splitter import Script_Blocks_Container, Block
import Classifier
from Marker import Marker
import unittest

# Test module for the microservice

class BlockDeepMarkerTest(unittest.TestCase):
    
    # CategoryConverter ==============================
    def test_get_right_category_number(self):
        converter = CategoryConverter('testFiles/category_map_test.csv')
        self.assertIs(converter.get_category_number('ok'), 0)
        self.assertIs(converter.get_category_number('magic'), 29)
        self.assertIs(converter.get_category_number('minustenk'), 30)
        self.assertEquals(converter.get_category(30), 'minustenk')
        self.assertEquals(converter.get_category(3), 'unimplemented')
    
    def test_maps(self):
        converter = CategoryConverter('testFiles/category_map_test.csv')
        annotation_m = converter.annotation_map
        error_m = converter.error_map
        self.assertEquals(annotation_m['ok'], 'ok')
        self.assertEquals(annotation_m['hs9527549314'], 'don\'t write rubbish')
        self.assertEquals(error_m['ok'], 'unknown')
        self.assertEquals(error_m['hs9527549314'], 'semantic')

    # MLUtils =======================================
    def test_parse_training(self):
        parsed = MLUtils.parse_training_file('testFiles/training.txt')
        self.assertTrue(('ok', ' &#124; otherwise = y') in parsed)
        self.assertTrue(('comment', '-- Geometric sequence') in parsed)

    def test_format(self):
        formatted = MLUtils.format_line('abcde')
        expected = [97, 98, 99, 100, 101]
        self.assertEquals(formatted, expected)
    
    # Splitter =======================================
    def test_splitter(self):
        splitter = Script_Blocks_Container('testFiles/split_test.txt', 80, 20)
        splitter.split()
        splitted = splitter.container
        self.assertIs(len(splitted), 5)
        last_but_one_block = splitted[3]
        expected_c = 'ddddddddddddddddddd\neeeeeeeeeeeeeeeeeeeeffffffffffffffffffffgggggggggggggggggggg'
        self.assertEquals(last_but_one_block.content, expected_c)
        self.assertIs(last_but_one_block.lineno, 1)

    # Marker =========================================
    def test_format_training(self):
        marker = Marker('testFiles/training.txt', None, 'testFiles/category_map_test.csv', 550)
        formatted = marker.format_training_file()
        form_line = MLUtils.format_line((' &#124; otherwise = y\n\n').replace('\n',''))
        padded = MLUtils.pad_float(form_line, 550)
        self.assertTrue((0, padded) in formatted)
        

if __name__ == '__main__':
    unittest.main()
