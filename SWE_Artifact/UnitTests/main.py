import unittest

class TestCollection(unittest.TestCase):

    def setUp(self):
        self.collection = []

    #Creation Test
    def test_is_empty_on_creation(self):
        self.assertEqual(len(self.collection), 0)

    #Size Test
    def test_size_changes_when_items_added(self):
        self.collection.append(1)
        self.assertEqual(len(self.collection), 1)
        self.collection.append(2)
        self.assertEqual(len(self.collection), 2)


    #Access Test with Valid Index
    def test_access_valid_index(self):
        self.collection.extend([10, 20, 30])
        self.assertEqual(self.collection[1], 20)


    #Access Test with Invalid Index
    def test_access_invalid_index_throws(self):
        with self.assertRaises(IndexError):
            _ = self.collection[5]


    #Removing Items test
    def test_pop_back_reduces_size(self):
        self.collection.extend([1, 2, 3])
        self.collection.pop()
        self.assertEqual(len(self.collection), 2)

    #Negative test for popping empty collectin
    def test_pop_on_empty_throws(self):
        with self.assertRaises(IndexError):
            self.collection.pop()

    #Clear Test
    def test_clear_empties_collection(self):
        self.collection.extend([5, 6, 7])
        self.collection.clear()
        self.assertEqual(len(self.collection), 0)


if __name__ == '__main__':
    unittest.main()


