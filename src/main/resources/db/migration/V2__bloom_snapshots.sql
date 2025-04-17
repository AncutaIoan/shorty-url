CREATE TABLE bloom_snapshot_chunks (
                                      id SERIAL PRIMARY KEY,  -- Auto-incremented primary key
                                      name VARCHAR(255) NOT NULL,  -- Name of the snapshot
                                      snapshot BYTEA NOT NULL,  -- Bloom filter snapshot as a byte array
                                      chunk_id INT NOT NULL,  -- The chunk identifier
                                      created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP  -- Timestamp of when the record was created
);
