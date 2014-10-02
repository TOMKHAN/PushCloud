<?php

class DBFunction {

    private $db;

    public function __construct($database) {
        $this->db = $database;
    }

    public function storeUser($gcm_regid) {
        $query = $this->db->prepare("SELECT * FROM gcm_users WHERE gcm_regid = ?");
        $query->bindValue(1, $gcm_regid);

        try {
            $query->execute();
            $alreadyIsUser = $query->fetchColumn();
            if ($alreadyIsUser == false) {
                $query = $this->db->prepare("INSERT INTO gcm_users(gcm_regid) VALUES(?)");
                $query->bindValue(1, $gcm_regid);
                $query->execute();
                return "registered";
                /* $query2 = $this->db->prepare("SELECT * FROM gcm_users WHERE id = ?");
                  $id_zadnjeg_usera = $this->get_next_autoincrement_number();
                  $query2->bindValue(1, $id_zadnjeg_usera);
                  $query2->execute();
                  $row = $query2->fetchColumn();
                  if ($row > 0) {
                  return true;
                  } else {
                  return false;
                  } */
            } else {
                return "already_here";
            }
        } catch (PDOException $e) {
            die($e->getMessage());
            return false;
        }
    }

    public function deleteUser($gcm_regid) {
        $query = $this->db->prepare("SELECT * FROM gcm_users WHERE gcm_regid = ?");
        $query->bindValue(1, $gcm_regid);

        try {
            $query->execute();
            $alreadyIsUser = $query->fetchColumn();
            if ($alreadyIsUser) {
                //$query = $this->db->prepare("DELETE FROM gcm_users WHERE gcm_regid = ?");
                //$query->bindValue(1, $gcm_regid);
                //$query->execute();
                return "deleted";
                /* todo obrisati i iz subscriptiona isto! */
            } else {
                return "no_such_user";
            }
        } catch (PDOException $e) {
            die($e->getMessage());
            return false;
        }
    }

    public function insertOrDeleteNewSubscriber($gcm_regid, $name_of_sponsor) {
        $query = $this->db->prepare("SELECT * FROM gcm_subscriptions WHERE gcm_regid = ? AND sponsor_name = ?");
        $query->bindValue(1, $gcm_regid);
        $query->bindValue(2, $name_of_sponsor);

        try {
            $query->execute();
            $alreadyHadSubscribed = $query->fetchColumn();
            $querySponsorName = $this->db->prepare("SELECT * FROM gcm_sponsors WHERE name = ?");
            $querySponsorName->bindValue(1, $name_of_sponsor);
            $querySponsorName->execute();
            $sponsorExists = $querySponsorName->fetchColumn();
            if ($sponsorExists) {
                if ($alreadyHadSubscribed) {
                    $queryDelete = $this->db->prepare("DELETE FROM gcm_subscriptions WHERE gcm_regid = ? AND sponsor_name = ?");
                    $queryDelete->bindValue(1, $gcm_regid);
                    $queryDelete->bindValue(2, $name_of_sponsor);
                    $queryDelete->execute();
                    return "deleted";
                } else {
                    $queryInsert = $this->db->prepare("INSERT INTO gcm_subscriptions(gcm_regid,sponsor_name) VALUES(?,?)");
                    $queryInsert->bindValue(1, $gcm_regid);
                    $queryInsert->bindValue(2, $name_of_sponsor);
                    $queryInsert->execute();
                    return "added";
                }
            } else {
                return "no_sponsor";
            }
        } catch (PDOException $e) {
            return "error_with_writing";
            die($e->getMessage());
        }
    }

    public function insertNewSponsor($sponsor_name, $sponsor_image_path) {
        $query = $this->db->prepare("SELECT * FROM gcm_sponsors WHERE name = ?");
        $query->bindValue(1, $sponsor_name);

        try {
            $query->execute();
            $alreadyIssponsor = $query->fetchColumn();
            if ($alreadyIssponsor == false) {
                $query = $this->db->prepare("INSERT INTO gcm_sponsors(name, sponsor_image) VALUES(?,?)");
                $query->bindValue(1, $sponsor_name);
                $query->bindValue(2, $sponsor_image_path);
                $query->execute();
                return "ok";
            } else {
                return "not";
            }
        } catch (PDOException $e) {
            die($e->getMessage());
            return "write_error";
        }
    }
	
	public function deleteSponsor($sponsor_name){
		$querySponsorName = $this->db->prepare("SELECT * FROM gcm_sponsors WHERE name = ?");
        $querySponsorName->bindValue(1, $sponsor_name);
		try {
			$querySponsorName->execute();
			$sponsorExists = $querySponsorName->fetchColumn();
			if ($sponsorExists) {
				$queryDelete = $this->db->prepare("DELETE FROM gcm_sponsors WHERE name = ?");
				$queryDelete->bindValue(1, $sponsor_name);
		
				$queryDelete->execute();
				$queryDelete2 = $this->db->prepare("DELETE FROM gcm_subscriptions WHERE sponsor_name = ?");
				$queryDelete2->bindValue(1, $sponsor_name);
				$queryDelete2->execute();
				return "true";
			} else {
				return "error";
			}
			
		} catch (PDOException $e) {
			die($e->getMessage());
            return "write_error";
        }
	}

    public function getAllUsersForSubscriber($subName) {
        if ($subName == 'PushCloud') {
            $query = $this->db->prepare("select * FROM gcm_users");
            try {
                $query->execute();
                return $query->fetchAll();
            } catch (PDOException $e) {
                die($e->getMessage());
            }
        } else {
            $query = $this->db->prepare("select * FROM gcm_subscriptions WHERE sponsor_name = ?");
            $query->bindValue(1, $subName);
            try {
                $query->execute();
                return $query->fetchAll();
            } catch (PDOException $e) {
                die($e->getMessage());
            }
        }
    }

    public function getAllSponsors() {
        $query = $this->db->prepare("select * FROM gcm_sponsors");
        try {
            $query->execute();
            return $query->fetchAll();
        } catch (PDOException $e) {
            die($e->getMessage());
        }
    }

    /**
     * Returns number of users.
     * @return int
     */
    public function getNumberOfUsersForSubscriber($subName) {
        if ($subName == 'PushCloud') {
            $query = $this->db->prepare("SELECT COUNT(`id`) FROM `gcm_users`");
            try {
                $query->execute();
                $rows = $query->fetchColumn();
                return $rows;
            } catch (PDOException $e) {
                die($e->getMessage());
            }
        } else {
            $query = $this->db->prepare("SELECT COUNT(`id`) FROM `gcm_subscriptions` WHERE sponsor_name = ?");
            $query->bindValue(1, $subName);
            try {
                $query->execute();
                $rows = $query->fetchColumn();
                return $rows;
            } catch (PDOException $e) {
                die($e->getMessage());
            }
        }
    }

    public function get_next_autoincrement_number() {
        //$query = $this->db->prepare("SELECT AUTO_INCREMENT FROM information_schema.tables WHERE TABLE_NAME = 'gcm_users'");
        $query = $this->db->prepare("SELECT MAX(id) from gcm_users");
        try {
            $query->execute();
            $number = $query->fetchColumn();
            return $number + 1;
        } catch (PDOException $e) {
            die($e->getMessage());
        }
    }

}
