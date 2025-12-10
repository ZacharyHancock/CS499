import sqlite3
import random
import re

user_record = tuple
STR_WHERE = " where "


# inputs: db - sqlite3 database, sql string - query statement
# run_query: runs the string sql query to execute in the sqlite3 db
def run_query(db, sql):

    #SQL injection detection utilizing reges
    injections = re.compile(r"(\bor\b|\band\b).*=.*|--|;|union\s+select", re.IGNORECASE)

    #if injection found print fail statement and return from function
    if injections.search(sql):
        print("SQL Injection detected. Query Rejected.")
        return None

    #try to execute sql query and return rows, return error statement if failed
    try:
        cursor = db.execute(sql)
        rows = cursor.fetchall()
        return rows
    except sqlite3.Error as e:
        print(f"Data failed to be queried. ERROR = {e}")


#inputs: db - sqlite3 database
# initialize_database: creates users table and inserts dummy data
def initialize_database(db):

    #try to create USERS table
    try:
        db.execute("""
                   CREATE TABLE USERS(
                    ID INT PRIMARY KEY NOT NULL,
                    NAME TEXT NOT NULL,
                    PASSWORD TEXT NOT NULL
                    );
                    """)
        print("USERS table created.")
    except sqlite3.Error as e:
        print(f"Failed to create USERS table. ERROR = {e}")
        return False

    #try to insert data into USERS table
    try:
        db.executescript("""
                            INSERT INTO USERS VALUES (1, 'Fred', 'Flinstone');
                            INSERT INTO USERS VALUES (2, 'Barney', 'Rubble');
                            INSERT INTO USERS VALUES (3, 'Wilma', 'Flinstone');
                            INSERT INTO USERS VALUES (4, 'Betty', 'Rubble');
                            """)
        return True
    except sqlite3.Error as e:
        print(f"Failed to insert dummy data. ERROR = {e}")
        return False

# inputs: db - sqlite3 database, sql string - query statement
# run_query_injection: randomly generates a sql injection query, and tries to run it
def run_query_injection(db, sql):

    injected_sql = sql
    lc = sql.lower()

    # check for where statement in query then remove trailing semicolon if present and randomly insert SQL injection
    if STR_WHERE in lc:
        if injected_sql.endswith(";"):
            injected_sql = injected_sql[:-1]

        injected_options = [
            " or 1=1;",
            " or 2=2;",
            " or 'hi'='hi'",
            " or 'hack'='hack';"
        ]

        #insert random sql injection
        injected_sql += random.choice(injected_options)

    return run_query(db, injected_sql)


# inputs: sql string - query statement,  records - list of rows returned
# dump_results: prints SQL and all returned user records
def dump_results(sql, records):
    if records is None:
        return

    #print query and number of records, then iterate of records and print
    print(f"\nSQL: {sql} ==> {len(records)} records found.")
    for rec in records:
        print(f"User: {rec[1]} [UID={rec[0]} PWD={rec[2]}]")

# input: db - sqlite3 database
# run_queries: runs different valid and SQL injections to test code logic
def run_queries(db):
    records = None

    # Query all records
    sql = "SELECT * FROM USERS"
    records = run_query(db, sql)
    if records is None:
        return
    dump_results(sql, records)

    # Query a single records with NAME Fred
    sql = "SELECT ID, NAME, PASSWORD FROM USERS WHERE NAME='Fred'"
    records = run_query(db, sql)
    if records is None:
        return
    dump_results(sql, records)

    #Run query with injected payloads
    for _ in range(5):
        records = run_query_injection(db, sql)
        if records is not None:
            dump_results(sql, records)


def main():
    print("SQL Injection Example")

    db = sqlite3.connect(":memory:")

    print("Connected to database.")

    if not initialize_database(db):
        print("Database Initialization Failed. Terminating")
        return

    run_queries(db)

    db.close()

if __name__ == '__main__':
    main()