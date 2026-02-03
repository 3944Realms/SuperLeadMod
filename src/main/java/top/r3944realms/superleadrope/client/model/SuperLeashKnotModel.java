/*
 *  Super Lead rope mod
 *  Copyright (C)  2026  R3944Realms
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package top.r3944realms.superleadrope.client.model;


import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

/**
 * The type Super leash knot model.
 *
 * @param <T> the type parameter
 */
@OnlyIn(Dist.CLIENT)
public class SuperLeashKnotModel<T extends Entity> extends HierarchicalModel<T> {
	private static final String KNOT = "knot";
	private final ModelPart root;
	private final ModelPart knot;

    /**
     * Instantiates a new Super leash knot model.
     *
     * @param root the root
     */
    public SuperLeashKnotModel(ModelPart root) {
		this.root = root;
		this.knot = root.getChild(KNOT);
	}

    /**
     * Create body layer layer definition.
     *
     * @return the layer definition
     */
    public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();
		partdefinition.addOrReplaceChild(KNOT,
				CubeListBuilder
						.create()
						.texOffs(0, 0)
						.addBox(-3.0F, -8.0F, -3.0F, 6.0F, 8.0F, 6.0F),
				PartPose.ZERO
		);
		return LayerDefinition.create(meshdefinition, 32, 32);
	}

	@Override
	public void setupAnim (
			@NotNull Entity entity,
			float limbSwing,
			float limbSwingAmount,
			float ageInTicks,
			float netHeadYaw,
			float headPitch
	) {
		this.knot.yRot = netHeadYaw * 0.017453292F;
		this.knot.xRot = headPitch * 0.017453292F;
	}
	@Override
	public @NotNull ModelPart root() {
		return root;
	}
}