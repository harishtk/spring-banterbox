package space.banterbox.feature.users.repository;

import org.springframework.data.repository.CrudRepository;
import space.banterbox.feature.users.model.Profile;

public interface ProfileRepository extends CrudRepository<Profile, Long> {
}