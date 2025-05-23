import axios from 'axios'
import React, { Component } from 'react'
import { connect } from 'react-redux'
import { check } from '../../utils/securityUtils'
import RedirectFormButton from '../RedirectFormButton'
import CategoryCard from './CategoryCard'
import PropTypes from "prop-types";

class CategoryDashboard extends Component {
    constructor() {
        super()
        this.state = {
            categories: [],
            errors: {
                deleted : {},
                categoriesErrorStatus: "",
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
                this.setState({ errors: {...this.state.errors, deleted: {["id" + id]: error.response.data}}})
            }

        }
    }

    async componentDidMount() {
        check()
        const selectedLab = this.props.selectedLab
        this.loadCategories(selectedLab)
    }

    async componentWillReceiveProps(nextProps){
        const selectedLab = nextProps.selectedLab
        this.loadCategories(selectedLab)
    }


    async loadCategories(selectedLab) {
        if (selectedLab && JSON.stringify(selectedLab) !== "{}") {
            try {
                await axios.get(`/api/logged-in/chem-category/${selectedLab.value}`).then(result => this.setState({categories: result.data}))
            } catch (error) {
                console.log("error in get chem-categories: " + error)
                this.setState({ errors: {...this.state.errors, categoriesErrorStatus: error.response.status}})
            }

            console.log("in componentWillReceiveProps " + JSON.stringify(selectedLab))
            this.setState({selectedLab: selectedLab});
        }
    }

        

    render() {
        const isAdmin = (this.props.selectedLab.key) && 
                        (this.props.user.labsAsAdmin.includes(this.props.selectedLab.value) || 
                        this.props.selectedLab.labManagers.map(manager => manager.username).includes(this.props.user.username))
        return (
            <div className="categories">
                <div className="container">
                    <div className="row"> 
                        <div className="col-md-12">
                            <h1 className="display-4 text-center">Chemical Categories</h1>
                            <p className="lead text-center">With the help of these categories, shelf lifes of the chemicals can be calculated, after opened</p>
                            <br />
                            { isAdmin && 
                                (<RedirectFormButton formRoute="/add-category" buttonLabel="Add Category"/>)
                            }
                            <br />
                            <hr />
                            {this.props.selectedLab.key ? 
                                this.getCards(isAdmin) :
                                <p className="lead"><i>Please select a lab</i></p>
                            }
                           
                        </div>
                    </div>
                </div>
            </div>
        ) 
    }

    getCards(isAdmin) {
        return this.state.categories.map(category => (
            <CategoryCard 
                lab={this.props.selectedLab}
                isAdmin={isAdmin}
                category={category} 
                key={category.id} 
                deleteCategory={this.deleteCategory} 
                errors={this.state.errors.deleted["id" + category.id] ? this.state.errors.deleted["id" + category.id] : {}}
            />))
    }
}

CategoryDashboard.propTypes = {
    selectedLab: PropTypes.object.isRequired
}

const mapStateToProps = state => ({
    selectedLab: state.selectedLab,
    user: state.security.user
})

export default connect(mapStateToProps) (CategoryDashboard)

