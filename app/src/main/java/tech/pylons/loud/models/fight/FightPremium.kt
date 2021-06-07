package tech.pylons.loud.models.fight

class FightPremium(
    id: Int,
    name: String,
    hp: Int,
    attack: Int,
    reward: String,
    requirements: List<String>,
    conditions: List<String>,
    val cost: Long
) : Fight(id, name, hp, attack, reward, requirements, conditions)