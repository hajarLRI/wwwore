package roundRobin;

import java.util.LinkedList;
import java.util.List;

public class SimpleGenerator implements WorkloadGenerator<Integer> {

	@Override
	public List<User<Integer>> generate(int clients, int itemsPerUser, double overlap) {
		List<User<Integer>> users = new LinkedList<User<Integer>>();
		for(int i=0; i < clients; i++) {
			User<Integer> user = new User<Integer>();
			for(int j=0; j < itemsPerUser; j++) {
				Integer interest = (int) Math.ceil(((i*itemsPerUser) * (1 - overlap) + j));
				System.out.println("(" + i + ", " + interest);
				user.addInterest(interest);
			}
			users.add(user);
		}
		return users;
	}
	
	public static void main(String[] args) {
		SimpleGenerator s = new SimpleGenerator();
		s.generate(10, 10, .75);
	}

}
