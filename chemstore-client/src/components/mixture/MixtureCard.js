import { useState } from "react";
import useCollapse from "react-collapsed";
import DuButtons from "../UI/DuButtons";
import VerifyPanel from "../UI/VerifyPanel";
import IngredientMixtureTable from "./IngredientMixtureTable";

const MixtureCard = props => {

    const mixture = props.mixture
    const recipe = mixture.recipe

    const [deletionConfirmation, setDeletionConfirmation] = useState(false)

    const { getCollapseProps, getToggleProps } = useCollapse();

    const getChemicalRow = chemicalIngredient => {
        const chemicalId = chemicalIngredient.ingredient.id
        console.log(chemicalId)
        const chemItem = mixture.chemItems.filter(chemItem => chemItem.chemical.id === chemicalId)[0]
        console.log(chemItem)
        const chemical = chemItem.chemical
        return ({
            id: chemicalId,
            name: chemical.shortName,
            amount: chemicalIngredient.amount / recipe.amount * mixture.amount,
            unit: chemicalIngredient.unit,
            manufacturerName: chemItem.manufacturer.name,
            seqNumber: chemItem.seqNumber,
            batchNumber: chemItem.batchNumber,
            expirationDate: chemItem.expirationDate

        })
    }

    return (
        <div className="container card card-body bg-light mb-3" style={{paddingTop: "5px", paddingBottom: "0px", paddingRight: "10px", paddingLeft: "10px"}}>
            <div className="header row" {...getToggleProps()}>

                <div className="col-1">
                    <i className="mx-small">{mixture.id}</i>
                </div>
                <div className="col-3">
                    <h4 className="mx-auto">{recipe.name}</h4>
                </div>
                <div className="col-2">
                    <h4 className="mx-auto">{recipe.project.name}</h4>
                </div>
                <div className="col-sm-1">
                    <i>{mixture.amount} {recipe.unit}</i>
                </div>
                <div className="col-sm-2">
                    <i>{mixture.creationDate}</i><br/>
                    <i>(by {mixture.creator.fullName})</i>
                </div>
                <div className="col-sm-1">
                    <i>{mixture.expirationDate}</i>
                </div> 
                <div className="col-sm-2">
                    { props.isManager && 
                        <DuButtons 
                            updateDisabled={true}
                            updateFormTo={`/update-mixture/${mixture.id}`}
                            onDelete={() => setDeletionConfirmation(true)}
                        /> 
                    }
                </div>

            </div>
            <div {...getCollapseProps()}>
                <div className="content" style={{padding: "10px"}}>
                   
                    {(recipe.chemicalIngredients.length > 0  && 
                        <div style={{paddingBottom: "10px"}}>
                            <i><strong>Chemical Ingredients</strong></i>
                                <IngredientMixtureTable 
                                    mixture={mixture}
                                    type="CHEM_ITEM"
                                />
                        </div>
                    )}
                    {(recipe.recipeIngredients.length > 0  && 
                        <div style={{paddingBottom: "10px"}}>
                            <i><strong>Mixture Ingredients</strong></i>
                                <IngredientMixtureTable 
                                    mixture={mixture}
                                    type="MIXTURE"
                                />
                        </div>
                    )}
                </div>
                {deletionConfirmation && 
                    <VerifyPanel 
                        onCancel={() => setDeletionConfirmation(false)} 
                        veryfyMessage={`Are you sure you want to delete 
                            mixture ${recipe.name} (${mixture.identifier}, ${mixture.amount} ${recipe.unit}, created on ${mixture.creationDate})?`}
                        onSubmit={() => props.deleteMixture(mixture.id)}
                        buttonLabel="Delete"
                    />
                }
            </div>

        </div>
    )
}

export default MixtureCard