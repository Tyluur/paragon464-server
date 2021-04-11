ALTER TABLE paragon_player
ADD COLUMN last_address INET NOT NULL DEFAULT '0.0.0.0'::INET;
