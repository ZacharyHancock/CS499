from pymongo.mongo_client import MongoClient
from bson.objectid import ObjectId

#Global keys variable to hold key values of animals to compare 
keys = ['age_upon_outcome','animal_id','animal_type','breed','color','date_of_birth', 'datetime','monthyear',
        'name','outcome_subtype','outcome_type','sex_upon_outcome','location_lat','location_long','age_upon_outcome_in_weeks']


class AnimalShelter(object):
    """ CRUD operations for Animal collection in MongoDB """
    # variable to hold vlaue of keys of animals collection for comparison
    
    def __init__(self, userN, passW):
        # Initializing the MongoClient. This helps to 
        # access the MongoDB databases and collections.
        # This is hard-wired to use the aac database, the 
        # animals collection, and the aac user.
        # Definitions of the connection string variables are
        # unique to the individual Apporto environment.
        #
        # You must edit the connection variables below to reflect
        # your own instance of MongoDB!
        #
        # Connection Variables
        #
        USER = userN
        PASS = passW
        HOST = 'nv-desktop-services.apporto.com'
        PORT = 34630
        DB = 'AAC'
        COL = 'animals'
        #
        # Initialize Connection
        #
        self.client = MongoClient('mongodb://%s:%s@%s:%d' % (USER,PASS,HOST,PORT))
        self.database = self.client['%s' % (DB)]
        self.collection = self.database['%s' % (COL)]
        

        
# create: uses insert_one to add a new document into the collection, if it is successful it returns true if 
# unsuccessful it returns false
    def create(self, data):
        if data is not None:
            if list(data) == keys: #checks if the dict:data has the same keys
                insertion = self.database.animals.insert_one(data)  # data should be dictionary            
                return insertion.acknowledged   # returns acknowledged of insert_one which is a bool
            else:
                return False
        else:
            raise Exception("Nothing to save, because data parameter is empty")  #raises exception if no data entered

# read: uses find() to return a cursor that holds documents with the amthcing key:value pair, if its unable
# to find a mathcing doucment it returns an empty list
    def read(self, query):
        try:   ## try to catch any exceptions and handle it
            if query is not None:  # first checks if data is empty
                result = list(self.database.animals.find(query)) #finds animals with matching query
                return result
            else:  ## if there is no data then raise an exception that there is no data
                raise Exception("Nothing to find. Query is empty.")
                return False
        except Exception as e:  # exception raised if something errors
            print("Exception has occured: ", e)
            
# update: uses read function to see if the matched documents exist, if it does it updates all of themathcing documents in the collection and returns
# the number of documents updated          
    def update(self, query, newInfo):
        if self.read(query) != {}:      #calls read to see if the query returns a document
            result = self.database.animals.update_many(query, {"$set": newInfo})     #updates all documents that match the query
            return result.modified_count  #returns count of modified collections
        else:
            return 0
        
#delete: uses read function to see if the matched documents exist, if it does it dletes all of the matching documents in the collection and returns
# the number of documents deleted         
    def delete(self, query):
        if self.read(query) != {}:     #calls read to see if the query returns a document
            result = self.database.animals.delete_many(query)  #deletes all mathcing documents
            return result.deleted_count   #returns number of deleted documents
        else:
            return 0