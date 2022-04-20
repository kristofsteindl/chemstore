import React, { useState } from 'react'
import useCollapse from 'react-collapsed';
import DuButtons from '../UI/DuButtons';
import VerifyPanel from '../UI/VerifyPanel';
import IngredientRow from './IngredientRow';
import "./RecipeCard.css"

const RecipeCard = props => {
    const recipe = useState(props.recipe)[0]
    const [deletionConfirmation, setDeletionConfirmation] = useState(false)

    const getVerifyMessage = () => <p>{`Are you sure you want to delete `}<b>{recipe.name}</b></p>

    const { getCollapseProps, getToggleProps } = useCollapse();
        
    return (
        <div className="container">
           <div className="card card-body bg-light mb-3" style={{padding: "10px"}}>
                <div className="header row" {...getToggleProps()}>

                    <div className="col-4">
                        <h4 className="mx-auto">{recipe.name}</h4>
                    </div>
                    <div className="col-sm-1">
                        <i>{recipe.amount} {recipe.unit}</i>
                    </div>
                    <div className="col-sm-1">
                        <i>{recipe.shelfLifeInDays} day{recipe.shelfLifeInDays > 1 ? "s" : ""}</i>
                    </div>
                    <div className="col-3" />  
                    <div className="col-sm-3">
                        { props.isManager && 
                            <DuButtons 
                                updateFormTo={`/add-update-recipe/${recipe.id}`}
                                onDelete={() => setDeletionConfirmation(true)}
                            /> 
                            
                        }
                    </div>

                </div>
                <div {...getCollapseProps()}>
                <div className="content" style={{padding: "10px"}}>
                    {(recipe.chemicalIngredients.length > 0  && 
                    <div>
                        <p><strong>Chemical Ingredients</strong></p>
                        <ul>
                        {recipe.chemicalIngredients.map(ing => <IngredientRow key={ing.id} row={ing} />)}
                        </ul>
                    </div>
                    )}
                    {(recipe.recipeIngredients.length > 0  && 
                    <div><p><strong>Recipe Ingredients</strong></p>
                        <ul>
                            {recipe.recipeIngredients.map(ing => <IngredientRow key={ing.id} row={ing} />)}
                        </ul>
                    </div>
                    )}
                </div>
            </div>
           </div>
           {deletionConfirmation && 
                <VerifyPanel 
                    onCancel={() => setDeletionConfirmation(false)} 
                    veryfyMessage={`Are you sure you want to delete recipe ${recipe.name}?`}
                    onSubmit={() => props.deleteRecipe(recipe.id)}
                    buttonLabel="Delete"
                />
            }
        </div>
    )

}

export default RecipeCard 
