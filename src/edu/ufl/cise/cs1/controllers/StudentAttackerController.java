package edu.ufl.cise.cs1.controllers;
import game.controllers.AttackerController;
import game.models.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public final class StudentAttackerController implements AttackerController
{
	public void init(Game game) { }

	public void shutdown(Game game) { }

	public int update(Game game,long timeDue)
	{
		int action = -1;
		Attacker gator = game.getAttacker();
		List<Integer> directionOptions = gator.getPossibleDirs(true);
		List<Node> powerPills = game.getPowerPillList();
		List<Node> pills = game.getPillList();
		List<Defender> ghosts = game.getDefenders();
		List<Node> ghostLocations = ghostLocations(ghosts);

		boolean eat = false;
		List<Defender> targets = new ArrayList<Defender>();

		//Checks for vulnerable ghosts that are close
		for(int i = 0; i < ghosts.size();i++){
			//Decides to eat if there are vulnerable ghosts in close range
			if(ghosts.get(i).isVulnerable() && closestDistanceToPlayer(gator, ghostLocations) <= 30){
				eat = true;
				targets.add(ghosts.get(i));
			}
		}
		//Chases nearest vulnerable ghost
		if (eat){
			Actor target = gator.getTargetActor(targets, true);
			action = gator.getNextDir(target.getLocation(), true);
		}
		else {
			//Goes towards power pill if available and ghost is close
			if (powerPills.size() > 0){
				Node closestPowerPill = gator.getTargetNode(powerPills, true);
				if (closestDistanceToPlayer(gator, ghostLocations) <= 10){
					action = gator.getNextDir(closestPowerPill, true);
				}
				else{
					//Waits for defender to get close before eating power pill
					if(closestPowerPill.getPathDistance(gator.getLocation()) <= 5){
						action = gator.getReverse();
						return action;
					}
					else{
						action = gator.getNextDir(closestPowerPill, true);
						return action;
					}
				}
			}
		}
		//Goes after regular pills
		if((powerPills.size() == 0 || eat == false)){
			action = gator.getNextDir(gator.getTargetNode(pills,true),true);
		}

		//Run from non-vulnerable ghosts
		for(int i = 0; i < ghosts.size(); i++){
			if (ghosts.get(i).isVulnerable() == false){
				int distance = gator.getLocation().getPathDistance(ghostLocations.get(i));
				if (distance < 10 && distance > 0){
					action = gator.getNextDir(ghostLocations.get(i), false);
				}
			}
		}
		return action;
	}

	//Finds the value of the closest distance to the player from a list of node
	public int closestDistanceToPlayer(Attacker player, List<Node> node){
		int distance = player.getLocation().getPathDistance(node.get(0));
		for(int i = 1;i < node.size(); i++) {
			int currGhostDistance = player.getLocation().getPathDistance(node.get(i));
			if (currGhostDistance < distance){
				distance = currGhostDistance;
			}
		}
		return distance;
	}

	//Returns list of ghost locations
	public List<Node> ghostLocations (List<Defender> ghosts){
		List<Node> ghostLocations = new ArrayList<Node>();
		for (int i = 0; i < ghosts.size(); i++){
			ghostLocations.add(ghosts.get(i).getLocation());
		}
		return ghostLocations;
	}


}