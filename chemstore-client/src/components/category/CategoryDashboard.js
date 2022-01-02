import axios from 'axios'
import React, { Component } from 'react'
import { refreshTokenAndUser } from '../../securityUtils/securityUtils'
import NamedEntityCard from '../NamedEntityCard'
import RedirectFormButton from '../RedirectFormButton'
import CategoryCard from './CategoryCard'

export default class CategoryDashboard extends Component {
    constructor() {
        super()
        this.state = {
            categories: [],
            errors: {
                deleted : {},
                categoriesStatus: ""

        }
        }
        this.deleteCategory=this.deleteCategory.bind(this)
    }


    async deleteCategory(category) {
        const id = category.id
        if (window.confirm(`Are you sure you want to delete ${category.name}?`)) {
            try {
                await axios.delete(`/api/lab-admin/chem-category/${id}`)
                const refreshedCts = this.state.categories.filter(ctFromList => ctFromList.id !== id)
                this.setState({categories: refreshedCts})
            } catch (error) {
                this.setState({ errors: {deleted: {["id" + id]: error.response.data}}})
            }

        }
        console.log(`Hello ${category.name}`)
    }

    async componentDidMount() {
        refreshTokenAndUser()
        try {
            await axios.get('/api/lab-admin/chem-category?labKey=blab').then(result => this.setState({categories: result.data}))
        } catch (error) {
            console.log("error in get chem-categories: " + error.response.status)
            this.setState({ errors: {categoriesStatus: error.response.status}})
        }
    }
        

    render() {
        return (
            <div className="categories">
                <div className="container">
                    <div className="row"> 
                        <div className="col-md-12">
                            <h1 className="display-4 text-center">Chemical Categories</h1>
                            <p className="lead text-center">Categories of chemicals. With the help of these categories, you can specify shelf lifes for different categories for different lab</p>
                            <br />
                            <RedirectFormButton formRoute="/add-category" buttonLabel="Add Category"/>
                            <br />
                            <hr />
                            {this.state.categories.map(category => (
                                <CategoryCard 
                                    category={category} 
                                    key={category.id} 
                                    deleteCategory={this.deleteCategory} 
                                    errors={this.state.errors.deleted["id" + category.id] ? this.state.errors.deleted["id" + category.id] : {}}
                                />
                            ))
                                
                            }
                           
                        </div>
                    </div>
                </div>
            </div>
        ) 
    }
}

